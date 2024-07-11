package altai.restpayment.services;


import altai.restpayment.entities.UserEntity;
import altai.restpayment.repositories.RoleRepository;
import altai.restpayment.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userRepository.findByUsername(username) == null){
            throw new UsernameNotFoundException(String.format("Пользователь '%s' не найден", username));
        }
        UserEntity user = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public void save(UserEntity user) {
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
//public UserEntity createNewUser(RegistrationUserDto registrationUserDto) {
//    User user = new User();
//    user.setUsername(registrationUserDto.getUsername());
//    user.setEmail(registrationUserDto.getEmail());
//    user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
//    user.setRoles(List.of(roleService.getUserRole()));
//    return userRepository.save(user);
//}
}
