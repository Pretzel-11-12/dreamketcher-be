package pretzel.dreamketcherbe.common.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pretzel.dreamketcherbe.common.logging.MdcLoggingFilter;
import pretzel.dreamketcherbe.common.logging.QueryCounterInterceptor;
import pretzel.dreamketcherbe.common.logging.RequestLoggingFilter;

@Configuration
@RequiredArgsConstructor
public class LoggingConfig implements WebMvcConfigurer {

    private final QueryCounterInterceptor queryCounterInterceptor;

    private final HikariDataSource hikariDataSource;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(queryCounterInterceptor)
            .addPathPatterns("/**")
            .order(0);
    }

    @Bean
    public FilterRegistrationBean<Filter> mdcLoggingFilter() {
        FilterRegistrationBean<Filter> registrationBean =
            new FilterRegistrationBean<>();

        registrationBean.setFilter(new MdcLoggingFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean<Filter> requestLoiggingFilterRegistration() {
        FilterRegistrationBean<Filter> registrationBean =
            new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestLoggingFilter(hikariDataSource));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
