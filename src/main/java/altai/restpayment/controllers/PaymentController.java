package altai.restpayment.controllers;

import altai.restpayment.entities.UserEntity;
import altai.restpayment.services.PaymentService;
import altai.restpayment.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Невозможно найти пользователя по имени пользователя: " + principal.getName()));
        paymentService.processPayment(user.getId());
        return ResponseEntity.ok("Оплата прошла успешно");
    }
}
