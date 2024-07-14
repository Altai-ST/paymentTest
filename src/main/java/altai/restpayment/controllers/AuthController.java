package altai.restpayment.controllers;
import altai.restpayment.dtos.JwtRequest;
import altai.restpayment.dtos.JwtResponse;
import altai.restpayment.dtos.RegistrationUserDto;
import altai.restpayment.dtos.UserDto;
import altai.restpayment.entities.UserEntity;
import altai.restpayment.exceptions.AppError;
import altai.restpayment.services.LoginAttemptService;
import altai.restpayment.services.UserService;
import altai.restpayment.utils.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    private final HttpServletRequest request;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        String ipAddress = request.getRemoteAddr();

        boolean isAuthenticated = userService.authenticate(authRequest.getUsername(), authRequest.getPassword(), ipAddress);

        if (isAuthenticated) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            } catch (BadCredentialsException e) {
                return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
            }

            UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
            String token = jwtTokenUtils.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        }else{
            if(loginAttemptService.isBlocked(ipAddress)){
                return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Слишком много попыток, повторите через 5 минут"), HttpStatus.UNAUTHORIZED);
            }else{
                return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }
        UserEntity user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getBalance()));
    }
}
