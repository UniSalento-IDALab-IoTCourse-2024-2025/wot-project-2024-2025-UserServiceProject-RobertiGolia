package it.unisalento.iot2425.userserviceproject.service;

import it.unisalento.iot2425.userserviceproject.domain.User;
import it.unisalento.iot2425.userserviceproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //l'eccezione viene gestita da Spring

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.get().getEmail()).password(user.get().getPassword()).roles("USER").build(); //user.get perché è un Optionalf


        return userDetails;
    }
}