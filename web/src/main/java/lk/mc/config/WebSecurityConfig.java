package lk.mc.config;

import lk.mc.core.security.TsPasswordEncoder;
import lk.mc.filter.JwtRequestFilter;
import lk.mc.security.JwtAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * This class extends the WebSecurityConfigurerAdapter is a convenience
 * class that allows customization to both WebSecurity and HttpSecurity.
 *
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */

@SuppressWarnings("Duplicates")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private Environment environment;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MaintenanceInterceptor maintenanceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maintenanceInterceptor)
                .addPathPatterns("/crm-board/**",
                        "/vle/student/**",
                        "/user/**",
                        "/other-finance/**",
                        "/student/**",
                        "/student-program/**",
                        "/student-module/**",
                        "/vle/finance/my-course-payment-detail");
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // use TsPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return TsPasswordEncoder.getInstance();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        //configure the firewall instance....
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedPeriod(true);
        firewall.setAllowUrlEncodedDoubleSlash(true);
        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                .cors()
                .and()
                .authorizeRequests().antMatchers("/monitoring").permitAll()
                // all other requests need to be authenticated
                .anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    //    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        //todo REMOVE THIS
//        Calendar instance = Calendar.getInstance();
//        instance.set(Calendar.MONTH, 9);
//        instance.set(Calendar.YEAR, 2023);
//        instance.set(Calendar.DAY_OF_MONTH, 30);
//        if (new Date().after(instance.getTime())) {
//            return null;
//        }
//        // -------------------------------------------------
//
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                boolean isProduction = "production".equals(environment.getProperty("spring.profiles.active"));
////                if (isProduction) {
//                    logger.info("LMS runs on production mode");
//                    // lms
//                    registry.addMapping("/vle/**").allowedOrigins("https://www.metropolitancollege.lk",
//                            "https://metropolitancollege.lk",
//                            "https://sms.metropolitancollege.lk");
//
//                    // my fees
//                    registry.addMapping("/myfees/**").allowedOrigins("https://myfees.lk", "https://secure.myfees.lk");
//
//                    // admin
//                    registry.addMapping("/**").allowedOrigins("https://php.metropolitancollege.lk",
//                            "https://sms.metropolitancollege.lk");
//                    registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
////                } else {
////                    logger.info("LMS runs on development mode");
////                    registry.addMapping("/**").allowedOrigins("http://loalhost:1000", "http://loalhost:1001");
////                    registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
////                }
//
//            }
//        };
//    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
//        //todo REMOVE THIS
//        Calendar instance = Calendar.getInstance();
//        instance.set(Calendar.MONTH, 3);
//        instance.set(Calendar.YEAR, 2024);
//        instance.set(Calendar.DAY_OF_MONTH, 30);
//        if (new Date().after(instance.getTime())) {
//            return null;
//        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        boolean isProduction = "production".equals(environment.getProperty("spring.profiles.active"));
        if (isProduction) {
            logger.info("LMS runs on production mode");
            // lms
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList(
                    "https://www.metropolitancollege.lk",
                    "https://metropolitancollege.lk",
                    "http://212.47.73.190",
                    "https://php.metropolitancollege.lk",
                    "https://sms.metropolitancollege.lk",
                    "http://localhost:3000",
                    "http://localhost:3001"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configuration.setExposedHeaders(Collections.singletonList("x-auth-token"));
            source.registerCorsConfiguration("/vle/**", configuration);

            // my fees
            CorsConfiguration configurationMF = new CorsConfiguration();
            configurationMF.setAllowedOrigins(Arrays.asList("https://myfees.lk", "https://secure.myfees.lk"));
            configurationMF.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configurationMF.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configurationMF.setExposedHeaders(Collections.singletonList("x-auth-token"));
            source.registerCorsConfiguration("/myfees/**", configurationMF);

            // admin
            CorsConfiguration configurationAdmin = new CorsConfiguration();
            configurationAdmin.setAllowedOrigins(Arrays.asList(
                    "https://mnp.metropolitancollege.lk",
                    "http://212.47.73.190",
                    "http://localhost:3000",
                    "https://php.metropolitancollege.lk",
                    "https://sms.metropolitancollege.lk",
                    "http://localhost:3001"));
            configurationAdmin.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configurationAdmin.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configurationAdmin.setExposedHeaders(Collections.singletonList("x-auth-token"));
            source.registerCorsConfiguration("/**", configurationAdmin);

        } else {
            logger.info("LMS runs on development mode");
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001", "https://codepulse-b305c.web.app/"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configuration.setExposedHeaders(Collections.singletonList("x-auth-token"));
            source.registerCorsConfiguration("/**", configuration);
        }
        return source;
    }
}