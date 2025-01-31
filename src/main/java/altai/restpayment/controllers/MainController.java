package altai.restpayment.controllers;

import altai.restpayment.dtos.UserTransactionsDto;
import altai.restpayment.entities.UserEntity;
import altai.restpayment.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Невозможно найти пользователя по имени пользователя: " + principal.getName()));
        UserTransactionsDto userTransactionsDto = userService.getUserInfoWithTransactions(user.getId());
        return ResponseEntity.ok(userTransactionsDto);
    }
}
