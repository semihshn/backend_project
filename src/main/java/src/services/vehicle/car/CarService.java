package src.services.vehicle.car;

import src.controllers.vehicle.requests.car.CreateCarRequest;
import src.controllers.vehicle.requests.car.UpdateCarRequest;
import src.controllers.vehicle.responses.CarResponse;
import src.data.enums.default_data_enums.status.DefaultVehicleStatus;
import src.data.models.paperwork_entities.rentalEntities.RentalEntity;
import src.data.models.vehicle_entities.CarEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface CarService {

    void create(CreateCarRequest createCarRequest) throws IOException;

    CarResponse getById(int id);

    CarResponse update(UpdateCarRequest updateCarRequest) throws IOException;

    List<CarResponse> getAll();

    List<CarResponse> getAllFiltered(Integer customerID, Boolean licenseSuitable,
                                     LocalDate startDate, LocalDate endDate,
                                     Integer startPrice, Integer endPrice,
                                     Boolean isDeleted, Integer statusId,
                                     Integer colorId, Integer seat, Integer luggage, Integer modelId,
                                     Integer startYear, Integer endYear,
                                     Integer brandId, Integer fuelTypeID, Integer shiftTypeID, Integer segmentId);

    List<CarResponse> getAllByIsDrivingLicenseSuitable(Integer customerId);

    List<CarResponse> getAllByDeletedState(boolean isDeleted);

    List<CarResponse> getAllByStatus(Integer statusId);

    List<CarResponse> getAllByColorId(int id);

    List<CarResponse> getAllByModelId(int id);

    List<CarResponse> getAllByBrandId(int id);

    List<CarResponse> getAllByYearBetween(int startYear, int endYear);

    List<CarResponse> getAllByRentalPriceBetween(double startPrice, double endPrice);

    List<CarResponse> getAllByAvailabilityBetween(LocalDate startDate, LocalDate endDate);

    boolean isCarAvailableBetween(int carId, LocalDate startDate, LocalDate endDate);

    void delete(int id, boolean hardDelete) throws IOException;

    void softDelete(int id);

    void addRental(int carId, RentalEntity rentalEntity);

    void removeRental(int carId, RentalEntity rentalEntity);

    void changeStatus(CarEntity carEntity, DefaultVehicleStatus status);

    int getCountByDeletedState(boolean isDeleted);

    int getCountByStatusId(int statusId);
}
