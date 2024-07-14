package altai.restpayment.configs;

import altai.restpayment.services.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class BruteForceProtectionFilter extends OncePerRequestFilter {
    private final LoginAttemptService loginAttemptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = request.getRemoteAddr();
        if (loginAttemptService.isBlocked(ipAddress)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "IP-адрес заблокирован из-за слишком большого количества попыток входа в систему.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
