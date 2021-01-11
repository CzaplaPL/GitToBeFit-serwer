package pl.umk.mat.git2befit.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.repository.UserRepository;

import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService {
    private UserRepository userRepository;


    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<pl.umk.mat.git2befit.model.entity.User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        pl.umk.mat.git2befit.model.entity.User user = foundUser.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), emptyList());


    }
}
