package com.grupo_morado.sistema_facturacion_inventario.domain.models;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidEmailException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidFieldException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidPasswordException;
import lombok.Getter;

@Getter
public class User {

    private final String email;
    private final String password;
    private final String name;
    private final String lastname;
    private final StatusEnum status;
    private final Role role;


    public User(String email, String password, String name, String lastname, StatusEnum status, Role role){
        validateEmail(email);
        validatePassword(password);
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.role = role;
        this.status = status;
    }

    // ─── Métodos de validación de instancia ──────────────────────────────────
    public void validateEmail(String email){
        if(email == null || email.isBlank()){
            throw new InvalidEmailException("Correo electronico es nulo o vacío.");
        }

        if(email.length() >= 254){
            throw new InvalidEmailException("Correo electronico '" + email + "' muy largo.");
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(regex)){
            throw new InvalidEmailException("Correo electronico '" + email + "' es invalido.");
        }
    }

    public void validatePassword(String password){
        if(password == null || password.isBlank()){
            throw new InvalidPasswordException("Contraseña es nula o vacía.");
        }

        if(password.length() >= 254){
            throw new InvalidPasswordException("Contraseña '" + password + "' muy larga.");
        }
    }

    public void validateName(){
        if(name == null || name.isBlank()){
            throw new InvalidFieldException("El nombre del usuario es nulo o vacío.");
        }
    }

    public void validateLastname(){
        if(lastname == null || lastname.isBlank()){
            throw new InvalidFieldException("El apellido del usuario es nulo o vacío.");
        }
    }

    public void validateStatus(){
        if(status == null || status.toString().isBlank()){
            throw new InvalidFieldException("El estado del usuario es nulo o vacío.");
        }
    }

    // ─── Métodos de validación estáticos (para flujos sin instancia completa) ─

    /**
     * Valida el formato de un correo electrónico reutilizando las reglas de dominio.
     * Útil para flujos como recuperación de contraseña donde no se crea un User completo.
     *
     * @param email Correo a validar.
     * @throws InvalidEmailException si el formato es inválido.
     */
    public static void validateEmailFormat(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Correo electronico es nulo o vacío.");
        }
        if (email.length() >= 254) {
            throw new InvalidEmailException("Correo electronico '" + email + "' muy largo.");
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(regex)) {
            throw new InvalidEmailException("Correo electronico '" + email + "' es invalido.");
        }
    }

    /**
     * Valida el formato y complejidad de una contraseña elegida por el usuario.
     * Se aplica en flujos de creación o cambio de contraseña voluntario.
     *
     * <p>Reglas:
     * <ul>
     *   <li>No nula ni vacía.</li>
     *   <li>Entre 8 y 24 caracteres.</li>
     *   <li>Al menos una letra mayúscula (A-Z).</li>
     *   <li>Al menos una letra minúscula (a-z).</li>
     *   <li>Al menos un carácter especial (!@#$%&amp;*.,-).</li>
     * </ul>
     *
     * @param password Contraseña a validar.
     * @throws InvalidPasswordException si el formato o complejidad es inválido.
     */
    public static void validatePasswordFormat(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("La contraseña no puede estar vacía.");
        }
        if (password.length() < 8) {
            throw new InvalidPasswordException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (password.length() > 24) {
            throw new InvalidPasswordException("La contraseña no puede superar los 24 caracteres.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("La contraseña debe contener al menos una letra mayúscula.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new InvalidPasswordException("La contraseña debe contener al menos una letra minúscula.");
        }
        if (!password.matches(".*[!@#$%&*.,\\-].*")) {
            throw new InvalidPasswordException("La contraseña debe contener al menos un carácter especial (!@#$%&*.,-).");
        }
    }
}
