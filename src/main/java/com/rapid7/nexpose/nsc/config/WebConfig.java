package com.rapid7.nexpose.nsc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC configuration: registers the {@link AuthInterceptor} for all pages except
 * the login endpoints and static assets. (The JSP view resolver is configured via
 * spring.mvc.view.prefix/suffix in application.properties.)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // BUG (SI-3147): "/assets/**" was added here (meant to exclude a static
        // "/assets/img/**" folder that doesn't actually exist) and unintentionally
        // also matches the authenticated /assets and /assets/{id} pages, letting
        // them render without a session.
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/logout", "/css/**", "/img/**", "/js/**",
                        "/webjars/**", "/error", "/h2-console/**", "/favicon.ico",
                        "/assets/**");
    }
}
