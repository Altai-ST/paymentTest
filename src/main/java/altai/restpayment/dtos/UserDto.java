package altai.restpayment.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private double balance;
}
