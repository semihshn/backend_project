package source_files.services.BusinessRules.paperWork;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import source_files.data.models.paperWorkEntities.paymentEntities.PaymentDetailsEntity;
import source_files.data.models.paperWorkEntities.rentalEntities.RentalEntity;
import source_files.data.requests.paperworkRequests.RentalRequests.AddRentalRequest;
import source_files.data.requests.paperworkRequests.RentalRequests.ReturnRentalRequest;
import source_files.data.requests.paperworkRequests.RentalRequests.ShowRentalRequest;
import source_files.data.requests.paperworkRequests.paymentRequests.UpdatePaymentDetailsRequest;
import source_files.exception.DataNotFoundException;
import source_files.exception.NotSuitableException;
import source_files.exception.ValidationException;
import source_files.services.BusinessRules.abstractsBusinessRules.BaseBusinessRulesService;
import source_files.services.entityServices.paperWorkEntityManagers.RentalEntityManager;
import source_files.services.paperWorkServices.abstracts.DiscountCodeService;
import source_files.services.userServices.abstracts.CustomerService;
import source_files.services.vehicleService.abstracts.CarService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

import static source_files.exception.exceptionTypes.NotFoundExceptionType.RENTAL_LIST_NOT_FOUND;
import static source_files.exception.exceptionTypes.NotSuitableExceptionType.DRIVING_LICENSE_TYPE_NOT_SUITABLE;
import static source_files.exception.exceptionTypes.ValidationExceptionType.VALIDATION_EXCEPTION;

@AllArgsConstructor
@Service
public class RentalBusinessRules implements BaseBusinessRulesService {

    private final RentalEntityManager rentalEntityManager;

    private final DiscountCodeService discountCodeService;
    private final CarService carService;

    private final CustomerService customerService;

    //--------------------- AUTO FIX METHODS ---------------------
    public AddRentalRequest fixAddRentalRequest(AddRentalRequest addRentalRequest) {

        if (this.checkDiscountCodeIsNull(addRentalRequest.getDiscountCode())) {
            addRentalRequest.setDiscountCode(this.fixName(addRentalRequest.getDiscountCode()));
        }
        return addRentalRequest;
    }

    public ReturnRentalRequest fixReturnRentalRequest(ReturnRentalRequest returnRentalRequest) {

        return returnRentalRequest;
    }

    //---------------AUTO CHECKING METHODS--------------------------------

    public ShowRentalRequest checkShowRentalRequest(ShowRentalRequest showRentalRequest) {
        this.checkDrivingLicenseType(showRentalRequest.getCarEntityId(), showRentalRequest.getCustomerEntityId());
        this.carExists(showRentalRequest.getCarEntityId());
        this.checkDiscountCode(showRentalRequest.getDiscountCode());
        return showRentalRequest;
    }

    public AddRentalRequest checkAddRentalRequest(AddRentalRequest addRentalRequest) {
        this.userExists(addRentalRequest.getCustomerEntityId());
        return addRentalRequest;
    }

    public ReturnRentalRequest checkReturnRentalRequest(ReturnRentalRequest returnRentalRequest) {

        return returnRentalRequest;
    }


    //----------------------------METHODS--------------------------------


    public UpdatePaymentDetailsRequest createUpdatePaymentDetailsRequest(ReturnRentalRequest returnRentalRequest) {
        RentalEntity rentalEntity = this.rentalEntityManager.getById(returnRentalRequest.getRentalEntityId());
        UpdatePaymentDetailsRequest updatePaymentDetailsRequest = new UpdatePaymentDetailsRequest();

        PaymentDetailsEntity paymentDetailsEntity = rentalEntity.getPaymentDetailsEntity();

        updatePaymentDetailsRequest.setPaymentDetailsId(paymentDetailsEntity.getId());

        updatePaymentDetailsRequest.setPaymentType(paymentDetailsEntity.getPaymentTypeEntity().getPaymentType());
        //TODO  calculate total final amount kısmında hata var. cezalı hesaplamayı yanlış yapıyor.

        updatePaymentDetailsRequest.setAmount(
                this.calculateReturnFinalAmount(returnRentalRequest
                        , this.calculateTotalRentalDays(rentalEntity.getStartDate(), rentalEntity.getEndDate())));

        return updatePaymentDetailsRequest;
    }

    public void checkDrivingLicenseType(int carId, Integer customerId) {
        //Giriş yapmadan araç listeleyebilmek için customerId null verilebilmelidir.
        //CustomerId verilmiş ise ve beklenen ehliyet tipine uyuyorsa:
        if (customerId != null && !new HashSet<>(this.carService.getById(carId).getExpectedDrivingLicenseTypes()).
                containsAll(this.customerService.getById(customerId).getDrivingLicenseTypes())) {
            throw new NotSuitableException(DRIVING_LICENSE_TYPE_NOT_SUITABLE, "Ehliyet tipi uygun değil");
        }
    }

    @Override
    public List<?> checkDataList(List<?> list) {
        if (list.isEmpty()) {
            throw new DataNotFoundException(RENTAL_LIST_NOT_FOUND, "Aradığınız kriterlere uygun kiralama kaydı bulunamadı");
        }
        return list;
    }

    @Override
    public String fixName(String name) {
        return name.replace(" ", "").toUpperCase();
    }


    private void carExists(int carId) {
        this.carService.getById(carId);
    }

    private void userExists(int customerId) {
        this.customerService.getById(customerId);
    }

    public void checkDiscountCode(String discountCode) {
        if (this.checkDiscountCodeIsNull(discountCode)) { //discountCode girilmiş mi
            if (!this.discountCodeService.getByDiscountCode(discountCode).isActive()) // varsa aktifmi değilmi diye bakıyor.
            {
                throw new ValidationException(VALIDATION_EXCEPTION, "Bu indirim kodu artık geçersizdir.");
            }
        }
    }

    private boolean checkDiscountCodeIsNull(String discountCode) {
        return discountCode != null && !discountCode.equals("");
    }


    //----------------------------CALCULATING METHODS-------------------------------------


    public int calculateTotalRentalDays(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    private int calculateDelayDay(LocalDate endDate, LocalDate returnDate) {
        return Math.toIntExact(ChronoUnit.DAYS.between(endDate, returnDate));
    }

    public double calculateTotalBasePrice(int totalRentalDays, double rentalPrice) {
        return totalRentalDays * rentalPrice;
    }


    private double calculateTotalPriceWithDiscount(double baseTotalPrice, int discountPercent) {
        return baseTotalPrice - (baseTotalPrice * discountPercent / 100);
    }

    public double calculateReturnFinalAmount(ReturnRentalRequest returnRentalRequest, int totalRentalDays) {
        RentalEntity rentalEntity = this.rentalEntityManager
                .getById(returnRentalRequest.getRentalEntityId());

        double baseTotalPrice = calculateTotalBasePrice(totalRentalDays, rentalEntity.getCarEntity().getRentalPrice());

        if (returnRentalRequest.getReturnDate().isBefore(rentalEntity.getEndDate())) {//İlk if - zamanından erken getirirse indirim iptali.
            return baseTotalPrice;

        } else if (returnRentalRequest.getReturnDate().isAfter(rentalEntity.getEndDate())) { ////İkinci if - zamanından sonra teslim edildiyse.
            return baseTotalPrice + (calculateDelayDay(rentalEntity.getEndDate(), returnRentalRequest.getReturnDate()) * 100); //Gün başına 100 tl ceza ve indirim iptal.
        }

        return rentalEntity.getPaymentDetailsEntity().getAmount(); //zamanında getirirse
    }

    public double calculateAmount(ShowRentalRequest showRentalRequest) {

        // Discount kodu boşsa, indirim yapılmasına gerek yok
        if (this.checkDiscountCodeIsNull(showRentalRequest.getDiscountCode())) {

            return this.calculateTotalPriceWithDiscount(
                    this.calculateTotalBasePrice(
                            this.calculateTotalRentalDays(showRentalRequest.getStartDate(), showRentalRequest.getEndDate())
                            , this.carService.getById(showRentalRequest.getCarEntityId()).getRentalPrice()
                    )
                    , this.discountCodeService.getByDiscountCode(
                            showRentalRequest.getDiscountCode()).getDiscountPercentage()
            );
        } else {
            // Discount kodu yoksa, direkt kira ücretini kullan

            return this.calculateTotalBasePrice(
                    this.calculateTotalRentalDays(showRentalRequest.getStartDate(), showRentalRequest.getEndDate())
                    , this.carService.getById(showRentalRequest.getCarEntityId()).getRentalPrice()
            );
        }
    }
}