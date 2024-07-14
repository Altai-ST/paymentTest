package altai.restpayment.services;
import altai.restpayment.dtos.PaymentDto;
import altai.restpayment.dtos.RegistrationUserDto;
import altai.restpayment.dtos.UserTransactionsDto;
import altai.restpayment.entities.PaymentEntity;
import altai.restpayment.entities.UserEntity;
import altai.restpayment.repositories.PaymentRepository;
import altai.restpayment.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private RoleService roleService;
    @Lazy
    private PasswordEncoder passwordEncoder;
    private PaymentRepository paymentRepository;
    private LoginAttemptService loginAttemptService;

    private PaymentService paymentService;

    @Autowired
    public void setLoginAttemptService(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
//
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public boolean authenticate(String username, String rawPassword, String ipAddress) {// Получите IP адрес текущего запроса

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (loginAttemptService.isBlocked(ipAddress)) {
            throw new RuntimeException("IP address is blocked due to too many login attempts.");
        }

        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());

        if (passwordMatches) {
            loginAttemptService.loginSucceeded(ipAddress);
        } else {
            loginAttemptService.loginFailed(ipAddress);
        }

        return passwordMatches;
    }

    public UserEntity createNewUser(RegistrationUserDto registrationUserDto) {
        UserEntity user = new UserEntity();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setBalance(BigDecimal.valueOf(8.00));
        user.setRoles(List.of(roleService.getUserRole()));
        return userRepository.save(user);
    }


    public UserTransactionsDto getUserInfoWithTransactions(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        UserTransactionsDto userTransactionsDto = new UserTransactionsDto();
        userTransactionsDto.setId(userEntity.getId());
        userTransactionsDto.setUsername(userEntity.getUsername());
        userTransactionsDto.setBalance(userEntity.getBalance());

        List<PaymentEntity> payments = userEntity.getPayments();
        if (payments.isEmpty()) {
            userTransactionsDto.setPayments(Collections.emptyList());
            userTransactionsDto.setTransactionStatus("Не совершал транзакции");
        } else {
            List<PaymentDto> paymentDtos = payments.stream()
                    .map(payment -> {
                        PaymentDto paymentDto = new PaymentDto();
                        paymentDto.setId(payment.getId());
                        paymentDto.setAmount(payment.getAmount());
                        paymentDto.setTimestamp(payment.getTimestamp());
                        return paymentDto;
                    })
                    .collect(Collectors.toList());
            userTransactionsDto.setPayments(paymentDtos);
            userTransactionsDto.setTransactionStatus("Транзакции присутствуют");
        }

        return userTransactionsDto;
    }
}
