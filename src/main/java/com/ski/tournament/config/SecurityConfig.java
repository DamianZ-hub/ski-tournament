package com.ski.tournament.config;

import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @Bean
//    public CustomRequestCache requestCache() {
//        return new CustomRequestCache();
//    }

    @Bean
    public VaadinDefaultRequestCache requestCache() {
        return new VaadinDefaultRequestCache(){
            @Override
            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
                if (request.getServletPath().startsWith("/beacon/")) {
                    return;
                }
                super.saveRequest(request, response);
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()

                .requestCache().requestCache(requestCache())
                .and().authorizeRequests()
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage(LOGIN_URL).permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                .and()
                .logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    @Override
    public void configure(WebSecurity web) {

        web.ignoring().antMatchers(
                "/VAADIN/**",
                "/favicon.ico",
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                "/icons/**",
                "/images/**",
                "/node_modules/**",
                "/sw-runtime-resources-precache.js",
                "/frontend/**",
                "/webjars/**",
                "/activate/**",
                "/h2-console/**",
                "/frontend-es5/**",
                "/frontend-es6/**");
    }

//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        mailSender.setUsername("my.gmail@gmail.com");
//        mailSender.setPassword("password");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");
//
//        return mailSender;
//    }

}
