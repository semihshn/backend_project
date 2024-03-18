package src.controller.vehicle.car.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import src.service.vehicle.model.VehicleType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCarRequest {
    private final VehicleType vehicleType = VehicleType.CAR;
    @NotNull(message = "brandId null olamaz")
    @Min(1)
    int carImageEntityId;

    @NotNull(message = "brandId null olamaz")
    @Min(1)
    int brandEntityId;

    @NotNull(message = "id null olamaz")
    @Min(1)
    int carModelEntityId;

    @NotNull(message = "carBodyTypeId null olamaz")
    @Min(1)
    int carBodyTypeEntityId;

    @NotNull(message = "colorId null olamaz")
    @Min(1)
    int colorEntityId;

    @NotNull(message = "segmentId null olamaz")
    @Min(1)
    int carSegmentEntityId;

    @NotNull(message = "yıl null olamaz.")
    @Min(value = 2005, message = "Yıl en düşük 2005 olmalıdır.")
    @Max(value = 2024, message = "Yıl en yüksek 2024 olmalıdır.")
    int year;

    @Size(max = 500, message = "Acıklama en fazla 500 karakter olmalıdır.")
    String details;

    @DecimalMin(value = "100.0", message = "Kiralama ücreti 110 den küçük olamaz.")
    @NotNull(message = "rentalPrice can not be null")
    double rentalPrice;

    @NotNull(message = "License plate can not be null")
    @Pattern(regexp = "^(\\d{2}[ ]?[A-Za-z]{1,3}[ ]?\\d{2}|\\d{2}[ ]?[A-Za-z]{2}[ ]?\\d{3})$", message = "Invalid license plate format")
    String licensePlate;


    @NotNull(message = "kilometer null olamaz")
    @Min(value = 1, message = "Kilometre 1 den küçük olamaz.")
    int kilometer;

    @NotNull(message = "beklenen ehliyet sınıfı null olamaz")
    @Min(1)
    int expectedMinDrivingLicenseTypeId;

    @NotNull
    @Min(1)
    int shiftTypeEntityId;

    @NotNull
    @Min(1)
    int fuelTypeEntityId;

    @NotNull(message = "seat null olamaz")
    @Min(1)
    @Max(15)
    int seat;

    @NotNull(message = "luggage null olamaz")
    @Min(1)
    @Max(15)
    int luggage;

    @NotNull(message = "vehicleStatusId null olamaz")
    @Min(1)
    int vehicleStatusEntityId;

    @NotNull
    boolean isAvailable;

    @Override
    public String toString() {
        return "CreateCarRequest{" +
                "vehicleType=" + vehicleType +
                ", carImageEntityId=" + carImageEntityId +
                ", brandEntityId=" + brandEntityId +
                ", carModelEntityId=" + carModelEntityId +
                ", carBodyTypeEntityId=" + carBodyTypeEntityId +
                ", colorEntityId=" + colorEntityId +
                ", carSegmentEntityId=" + carSegmentEntityId +
                ", year=" + year +
                ", details='" + details + '\'' +
                ", rentalPrice=" + rentalPrice +
                ", licensePlate='" + licensePlate + '\'' +
                ", kilometer=" + kilometer +
                ", expectedMinDrivingLicenseTypeId=" + expectedMinDrivingLicenseTypeId +
                ", shiftTypeEntityId=" + shiftTypeEntityId +
                ", fuelTypeEntityId=" + fuelTypeEntityId +
                ", seat=" + seat +
                ", luggage=" + luggage +
                ", vehicleStatusEntityId=" + vehicleStatusEntityId +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
