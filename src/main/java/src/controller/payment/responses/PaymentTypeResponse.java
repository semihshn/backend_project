package src.controller.payment.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTypeResponse {
    int id;
    String name;
    boolean isActive;

    @Override
    public String toString() {
        return "PaymentTypeResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
