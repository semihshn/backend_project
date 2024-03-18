package src.controller.vehicle.features.car.segment.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCarSegmentRequest {
    @NotNull(message = "id null olamaz")
    int id;

    @NotBlank(message = "Segment adı boş geçilemez")
    @Size(min = 2, message = "Segment en az 2 karakter olmalıdır.")
    String name;

    @Override
    public String toString() {
        return "UpdateCarSegmentRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
