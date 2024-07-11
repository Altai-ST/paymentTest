package altai.restpayment.entities;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "amount")
    private double amount;
    @Column(name = "localDateTime")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
