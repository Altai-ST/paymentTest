package altai.restpayment.repositories;

import altai.restpayment.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
