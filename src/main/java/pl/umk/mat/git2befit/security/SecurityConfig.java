package pl.umk.mat.git2befit.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.umk.mat.git2befit.filter.JWTAuthenticationFilter;
import pl.umk.mat.git2befit.filter.JWTAuthorizationFilter;
import pl.umk.mat.git2befit.security.constraints.EquipmentConstraints;
import pl.umk.mat.git2befit.security.constraints.EquipmentTypeConstraints;

import static pl.umk.mat.git2befit.security.constraints.EquipmentConstraints.ALL_EQUIPMENTS;
import static pl.umk.mat.git2befit.security.constraints.EquipmentTypeConstraints.ALL_EQUIPMENT_TYPES;
import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.*;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsServiceImpl userDetailsService ;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder){
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requiresChannel(channel -> channel
                .anyRequest().requiresSecure())
                .cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL, FACEBOOK_LOGIN, GOOGLE_LOGIN, PASSWORD_REMIND).permitAll()
                .antMatchers(HttpMethod.GET, EMAIL_VERIFICATION).permitAll()
                // Zezwolenie na dostep do pobrania wszystkich sprzetow oraz wzgledem kategorii
                .antMatchers(HttpMethod.GET, ALL_EQUIPMENTS).permitAll()
                // Zezwolenie na dostep do pobrania wszystkich kategorii
                .antMatchers(HttpMethod.GET, ALL_EQUIPMENT_TYPES).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
