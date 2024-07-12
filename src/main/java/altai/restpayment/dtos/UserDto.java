package altai.restpayment.dtos;


import altai.restpayment.entities.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private BigDecimal balance;
}
