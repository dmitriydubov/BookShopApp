package com.example.MyBookShopApp.security.config;

import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import com.example.MyBookShopApp.security.service.AuthService;
import com.example.MyBookShopApp.security.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    private final UserDetailService userDetailService;
    private final CustomLogoutHandler customLogoutHandler;
    private final JWTRequestFilter filter;
    private final AuthService authService;

    @Autowired
    public SecurityConfig(UserDetailService userDetailService,
                          CustomLogoutHandler customLogoutHandler,
                          JWTRequestFilter filter,
                          @Lazy AuthService authService) {
        this.userDetailService = userDetailService;
        this.customLogoutHandler = customLogoutHandler;
        this.filter = filter;
        this.authService = authService;
    }

    @Bean
    BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .formLogin().loginPage("/signin").failureUrl("/signin")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/signin").deleteCookies("token")
                .addLogoutHandler(customLogoutHandler)
                .and().oauth2Login().successHandler(successHandler())
                .and().oauth2Client();

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    private AuthenticationSuccessHandler successHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            authService.registerOAuth2User(oAuth2User);

            httpServletResponse.sendRedirect("/my");
        };
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost:8085")
                .allowedMethods("*");
    }
}
