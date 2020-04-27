package com.example.ace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AceWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests(authorize -> authorize
                               .antMatchers("/").permitAll()
                               .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                                  .jwt(jwt -> jwt
                                       .decoder(jwtDecoder())));
    }

    private static OAuth2TokenValidator<Jwt>
        createDefaultWithIssuerAndAud(final String issuer, final String aud) {
        // Mostly copied from createDefaultWithIssuer in
        //   org.springframework.security.oauth2.jwt.JwtValidators.java, because
        //   there doesn't seem to be any way to add on the audience validator
        final List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(issuer));
        validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                                                           a -> a.contains(aud)));
        return new DelegatingOAuth2TokenValidator<>(validators);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        final NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
        jwtDecoder.setJwtValidator(createDefaultWithIssuerAndAud(issuerUri, "ace"));
        return jwtDecoder;
    }
}
