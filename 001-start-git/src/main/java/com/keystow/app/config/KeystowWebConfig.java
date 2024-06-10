package com.keystow.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan
public class KeystowWebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	// @Bean
	// public SpringTemplateEngine springTemplateEngine() {
	// SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
	//
	// springTemplateEngine.addDialect(new LayoutDialect());
	// springTemplateEngine.addDialect(new KeystowDialect());
	// return springTemplateEngine;
	// }

}
