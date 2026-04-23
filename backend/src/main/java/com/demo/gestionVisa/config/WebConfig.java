package com.demo.gestionVisa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
 
    /**
     * Permet de gérer les méthodes HTTP simulées via le champ _method.
     * @return HiddenHttpMethodFilter
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

     @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/statistics").setViewName("statistics/statistics");
    }
}