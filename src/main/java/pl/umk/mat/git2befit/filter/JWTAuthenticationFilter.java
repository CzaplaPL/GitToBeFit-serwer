package pl.umk.mat.git2befit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.umk.mat.git2befit.user.model.entity.User;
import pl.umk.mat.git2befit.config.security.service.JWTGenerator;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static pl.umk.mat.git2befit.config.security.constraints.SecurityConstraints.AUTHORIZATION;
import static pl.umk.mat.git2befit.config.security.constraints.SecurityConstraints.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/user/login-step");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User creds = new ObjectMapper().readValue(request.getInputStream(),  User.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
            //todo teoretycznie, jesli bedziemy chwytac wyjatek authenticationexception to sprawdzajac wiadomosc w srodku powinnismy byc w stanie dobrac odpowiednie bloki cause
        } catch (IOException e) {
            //todo zrobić loga i poprawic
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String token = JWTGenerator.generate((((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername()));
        response.addHeader(AUTHORIZATION, TOKEN_PREFIX + token);
    }
}
