package com.flujocaja.flujo_caja_api;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GenerarPassword {

    public static void main(String[] args) {

        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories
                        .createDelegatingPasswordEncoder();

        String password = "Admin1234";

        String hash =
                passwordEncoder.encode(password);

        System.out.println(hash);
    }
}