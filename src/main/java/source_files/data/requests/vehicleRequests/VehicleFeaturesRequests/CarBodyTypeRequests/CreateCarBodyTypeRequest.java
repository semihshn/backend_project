package source_files.data.requests.vehicleRequests.VehicleFeaturesRequests.CarBodyTypeRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source_files.data.enums.types.itemTypes.ItemType;
import source_files.data.requests.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCarBodyTypeRequest extends BaseRequest {
    private final ItemType itemType = ItemType.CAR_BODY_TYPE;
    @NotNull(message = "Body Type null olamaz")
    @NotBlank(message = "Body Type adı boş geçilemez")
    @Size(min = 2, message = "Body Type en az 2 karakter olmalıdır.")
    @Pattern(regexp = "^[a-zA-ZğüşıöçĞÜŞİÖÇ]+$", message = "Body Type sadece harflerden oluşmalıdır.")
    String carBodyTypeEntityName;
}
