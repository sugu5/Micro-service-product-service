package com.API.Gateway.config;

import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configurable
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers("/eureka/*").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt(jwtDecoder()));
        return serverHttpSecurity.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() throws NoSuchAlgorithmException {
        RSAPublicKey publicKey = (RSAPublicKey) generateRSAKeyPair().getPublic();
        return NimbusJwtDecoder.withPublicKey((java.security.interfaces.RSAPublicKey) publicKey).build();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}

}
