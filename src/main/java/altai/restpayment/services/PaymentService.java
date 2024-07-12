package altai.restpayment.services;


import altai.restpayment.entities.PaymentEntity;
import altai.restpayment.entities.UserEntity;
import altai.restpayment.repositories.PaymentRepository;
import altai.restpayment.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void processPayment(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        BigDecimal paymentAmount = BigDecimal.valueOf(1.10);
        if (user.getBalance().compareTo(paymentAmount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        user.setBalance(user.getBalance().subtract(paymentAmount));
        userRepository.save(user);

        PaymentEntity payment = new PaymentEntity();
        payment.setUser(user);
        payment.setAmount(paymentAmount);
        payment.setTimestamp(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
