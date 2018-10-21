package com.szakdolgozat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
	    return super.userDetailsService();
	}
	
	@Autowired
	private UserDetailsService userService;
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userService);
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}
	
	@Autowired
	public void configureAuth(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authProvider());
	}
	
	@Bean
    public AuthenticationSuccessHandler SzkdgAuthenticationSuccessHandler(){
        return new SzkdgSimpleUrlAuthenticationSuccessHandler();
    }

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/css/**", "/js/**", "/images/**").permitAll()
				.anyRequest().authenticated()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/registration").permitAll()
				.antMatchers("/reg").permitAll()
				.and()
			.formLogin()
				.loginPage("/login").permitAll()
				.successHandler(SzkdgAuthenticationSuccessHandler())
				.and()
			.logout()
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/login?logout")
				.permitAll();
	}
	
}
