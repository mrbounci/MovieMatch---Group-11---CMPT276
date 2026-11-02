package com.moviematch.moviematch.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginWebConfig implements WebMvcConfigurer {
    private final LoginRequiredInterceptor loginInterceptor;
    public LoginWebConfig(LoginRequiredInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                    "/",
                    "/movies/**",
                    "/groups/**",
                    "/voting/**"
                );
    }
}
