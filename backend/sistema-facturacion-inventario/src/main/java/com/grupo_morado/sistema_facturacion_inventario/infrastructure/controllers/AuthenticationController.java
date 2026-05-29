package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.AuthService;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.mappers.AuthenticationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthService authService;
    private final AuthenticationMapper authenticationMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDTO auth){
        log.info("Intento de inicio de sesion para el usuario con correo: {}", auth.email());
        User user = authenticationMapper.dtoToModelLogin(auth);
        AuthLoginResultDTO resultAuth = authService.login(user);
        log.info("Autenticacion exitosa para el usuario con correo: {}", auth.email());
        return ResponseEntity.ok(resultAuth);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthRegisterDTO auth){
        log.info("Intento de registro para el usuario con correo: {}", auth.email());
        AuthRegisterResultDTO resultAuth = authService.register(auth);
        log.info("Registro exitoso para el usuario con correo: {}", auth.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultAuth);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody )

}
