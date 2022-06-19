package com.decagon.chompapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http
                .cors().and().csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                .antMatchers("/checkout/**").hasAuthority("ROLE_PREMIUM")

                .antMatchers("/favorite/**").permitAll()
                .antMatchers("v3/api/-docs/**", "v2/api-docs/**",
                        "swagger-resources/**", "webjars/**")
                .permitAll()


                .antMatchers("/api/admin/create-product", "/api/admin/upload-image", "/api/admin/update-product-image/{id}", "/api/admin/update-product/{id}").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/v1/auth/users/getAllProducts/**").permitAll()
                .antMatchers("/api/v1/auth/users/orders").hasAuthority("ROLE_PREMIUM")
                .antMatchers("/api/admin/view-all-orders/{orderId}/update-order-status").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/v1/auth/users/getAllProducts/**").permitAll()
                .antMatchers("/api/v1/auth/users/display-user-details").hasAuthority("ROLE_PREMIUM")
                .antMatchers("/api/v1/auth/users/getAllProducts").permitAll()
                .antMatchers("/favorite/**").permitAll()
                .antMatchers("/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**").permitAll()

                .antMatchers("/home", "/company", "/faq",
                        "/contact", "/signup", "/confirmRegistration",
                        "/h2-console/**", "/login", "/logout", "/forgot_password",
                        "/api/v1/auth/login", "/verifyEmail", "/api/v1/auth/users/forgot-password",
                        "/api/v1/auth/users/enter-password", "/api/v1/auth/users/reset-password",
                        "/api/v1/auth/logout", "/cart/**")
                .permitAll()
                .antMatchers("/cart/add-to-cart/{productId}/**").permitAll()
                .antMatchers("/api/v1/auth/users/fetch-single-product").permitAll()
                .antMatchers("/admin").hasAuthority("ROLE_ADMIN")
                .antMatchers("api/v1/auth/wallet/fund-wallet").hasAuthority("ROLE_PREMIUM")
                .antMatchers("api/v1/auth/wallet/withdrawal").hasAuthority("ROLE_PREMIUM")
                .antMatchers("/orders").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/admin/delete-product/{productId}").hasAuthority("ROLE_ADMIN")
                .antMatchers( "/checkout",
                        "/wallet", "/order-history",
                        "/favorites", "/verifyEmail")

                .hasAnyAuthority("ROLE_ADMIN", "ROLE_PREMIUM")
                .antMatchers("/api/v1/auth/users/view-a-single-favorite-product").hasAnyAuthority("ROLE_ADMIN", "ROLE_PREMIUM")
                .antMatchers("/api/v1/auth/users/favorites").hasAnyAuthority("ROLE_ADMIN", "ROLE_PREMIUM")
                .antMatchers("/api/v1/auth/users/favorites/{productId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_PREMIUM")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers().frameOptions().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
