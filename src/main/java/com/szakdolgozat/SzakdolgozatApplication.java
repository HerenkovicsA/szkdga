package com.szakdolgozat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


@EnableConfigurationProperties
@PropertySources({
    @PropertySource(value = "file:${szkdg_conf}", ignoreResourceNotFound = true)
})
@SpringBootApplication
public class SzakdolgozatApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(SzakdolgozatApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	  return application.sources(SzakdolgozatApplication.class);
    }
}
