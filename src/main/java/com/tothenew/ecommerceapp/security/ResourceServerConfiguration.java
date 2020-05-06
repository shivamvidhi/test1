package com.tothenew.ecommerceapp.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    AppUserDetailsService userDetailsService;

    public ResourceServerConfiguration() {
        super();
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").anonymous()
                .antMatchers("/customer/re-sendActivation").permitAll()
                .antMatchers("/resetPassword").permitAll()
                .antMatchers("/token/**").permitAll()
                .antMatchers("/customer/activate/**").permitAll()
                .antMatchers("/register/**").permitAll()
                .antMatchers("/customer/profile/categories").permitAll()
                .antMatchers("/customer/profile/**").permitAll()
                .antMatchers("/customer/profile/**").permitAll()
                .antMatchers("/seller/profile/categories").permitAll()
                .antMatchers("/seller/profile/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/user/home").permitAll()
                .antMatchers("/metadata/**").permitAll()
                .antMatchers("/category/**").permitAll()
                .antMatchers("/categoryMetadata/**").permitAll()
                .antMatchers("/product/add/**").permitAll()
                .antMatchers("/product/view/**").permitAll()
                .antMatchers("/product/update/**").permitAll()
                .antMatchers("/product/delete/**").permitAll()
                .antMatchers("/product/customer/**").permitAll()
                .antMatchers("/product/admin/**").permitAll()
                .antMatchers("/productVariation/add/**").permitAll()
                .antMatchers("/productVariation/view/**").permitAll()
                .antMatchers("/productVariation/update/**").permitAll()
                .antMatchers("/doLogout").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/configuration/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable();
    }
}