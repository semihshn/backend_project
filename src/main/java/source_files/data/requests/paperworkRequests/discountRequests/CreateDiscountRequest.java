package source_files.data.requests.paperworkRequests.discountRequests;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDiscountRequest {
    @NotBlank(message = "Discount code cannot be blank")
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Discount code must consist of letters and numbers only")
    String discountCode;

    @Min(value = 5, message = "Discount percentage must be greater than or equal to 5")
    @Max(value = 90, message = "Discount percentage must be less than or equal to 90")
    @Pattern(regexp = "^[0-9]+$", message = "Discount percentage must consist of numbers only")
    @NotNull(message = "Discount percentage cannot be blank")
    int discountPercentage;
}
