package altai.restpayment.dtos;

import altai.restpayment.entities.PaymentEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class UserTransactionsDto {
    private Long id;
    private String username;
    private BigDecimal balance;
    private List<PaymentDto> payments;
    private String transactionStatus;
}
