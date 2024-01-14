package source_files.services.paperWorkServices;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import source_files.data.DTO.Mappers.ModelMapperService;
import source_files.data.DTO.paperWorkDTOs.PaymentDetailsDTO;
import source_files.data.DTO.paperWorkDTOs.RentalDTO;
import source_files.data.DTO.paperWorkDTOs.ShowRentalResponse;
import source_files.data.models.paperWorkEntities.paymentEntities.PaymentDetailsEntity;
import source_files.data.models.paperWorkEntities.paymentEntities.PaymentTypeEntity;
import source_files.data.models.paperWorkEntities.rentalEntities.RentalEntity;
import source_files.data.requests.paperworkRequests.RentalRequests.AddRentalRequest;
import source_files.data.requests.paperworkRequests.RentalRequests.ReturnRentalRequest;
import source_files.data.requests.paperworkRequests.RentalRequests.UpdateRentalRequest;
import source_files.data.requests.vehicleRequests.CarRequests.UpdateCarRequest;
import source_files.services.BusinessRules.paperWork.RentalBusinessRules;
import source_files.services.entityServices.abstracts.paperWorkAbstracts.RentalEntityService;
import source_files.services.paperWorkServices.abstracts.RentalService;
import source_files.services.systemServices.SysPaymentDetailsService;
import source_files.services.userServices.abstracts.CustomerService;
import source_files.services.vehicleService.abstracts.CarService;

import java.util.List;

import static source_files.data.types.ItemType.RENTAL;

@Service
@AllArgsConstructor
public class RentalManager implements RentalService {

    private final RentalEntityService rentalEntityService;
    private final ModelMapperService modelMapperService;
    private final CarService carService;
    private final SysPaymentDetailsService sysPaymentDetailsService;
    private final CustomerService customerService;
    private RentalBusinessRules rentalBusinessRules;

    @Override
    public ShowRentalResponse showRentalDetails(AddRentalRequest addRentalRequest) {

        return this.convertToShowRentalResponse(addRentalRequest);
    }

    @Override
    public RentalDTO add(AddRentalRequest addRentalRequest, PaymentDetailsDTO paymentDetailsDTO) {
        // indirim işlemleri sonucu totalPrice hesaplama

        RentalEntity rentalEntity = modelMapperService.forRequest()
                .map(rentalBusinessRules.checkAddRentalRequest(
                        rentalBusinessRules.fixAddRentalRequest(addRentalRequest)), RentalEntity.class);

        PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity(

                paymentDetailsDTO.getAmount()
                , new PaymentTypeEntity(
                paymentDetailsDTO.getPaymentTypeDTO().getPaymentTypeEntityName()
                , paymentDetailsDTO.getPaymentTypeDTO().getPaymentTypeEntityPaymentType())
        );

        rentalEntity.setPaymentDetailsEntity(paymentDetailsEntity);
        rentalEntity.setStartKilometer(carService.getById(addRentalRequest.getCarEntityId()).getKilometer());
        rentalEntity.setItemType(RENTAL);

        return this.modelMapperService.forResponse().map(rentalEntityService.add(rentalEntity), RentalDTO.class);
    }

    @Override
    public RentalDTO returnCar(ReturnRentalRequest returnRentalRequest) {
        // ceza işlemleri , indirim işlemleri iptali kontrol edilecek sonuçta da totalPrice güncelleme

        RentalEntity rentalEntity = this.rentalEntityService.getById(returnRentalRequest.getRentalEntityId());

        rentalEntity.setPaymentDetailsEntity(this.sysPaymentDetailsService.update(
                this.rentalBusinessRules.createUpdatePaymentDetailsRequest(returnRentalRequest)));

        rentalEntity.setActive(false);
        rentalEntity.getCarEntity().setKilometer(rentalEntity.getEndKilometer());
        this.carService.update(this.modelMapperService.forResponse().map(rentalEntity.getCarEntity(), UpdateCarRequest.class));
        return this.modelMapperService.forResponse().map(this.rentalEntityService.update(rentalEntity), RentalDTO.class);
    }

    @Override
    public RentalDTO update(UpdateRentalRequest updateRentalRequest) {
        return null;
    }

    @Override
    public RentalDTO getById(int id) {
        return this.modelMapperService.forResponse().map(rentalEntityService.getById(id), RentalDTO.class);
    }

    @Override
    public void delete(int id) {
        this.rentalEntityService.delete(this.rentalEntityService.getById(id));
    }

    @Override
    public void softDelete(int id) {

    }

    @Override
    public List<RentalDTO> getAll() {
        return this.rentalEntityService.getAll().stream()
                .map(rentalEntity -> modelMapperService.forResponse().map(rentalEntity, RentalDTO.class)).toList();
    }

    @Override
    public List<RentalDTO> getAllByIsDeletedFalse() {
        return null;
    }

    @Override
    public List<RentalDTO> getAllByIsDeletedTrue() {
        return null;
    }

    public ShowRentalResponse convertToShowRentalResponse(AddRentalRequest addRentalRequest) {

        ShowRentalResponse showRentalResponse = this.modelMapperService.forResponse()
                .map(this.rentalBusinessRules.checkAddRentalRequest(
                                this.rentalBusinessRules.fixAddRentalRequest(addRentalRequest)
                        ), ShowRentalResponse.class
                );//endDate ve startDate almak için mapledik.

        showRentalResponse.setCarDTO(carService.getById(addRentalRequest.getCarEntityId()));
        showRentalResponse.setCustomerDTO(customerService.getById(addRentalRequest.getCustomerEntityId()));
        showRentalResponse.setAmount(this.rentalBusinessRules.calculateAmount(addRentalRequest));

        return showRentalResponse;
    }
}
