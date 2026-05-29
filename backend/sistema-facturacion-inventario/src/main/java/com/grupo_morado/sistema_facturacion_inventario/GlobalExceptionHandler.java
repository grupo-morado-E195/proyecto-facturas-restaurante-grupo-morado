package com.grupo_morado.sistema_facturacion_inventario;

import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidCurrentPasswordException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidEmailException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidFieldException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidPasswordException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.PasswordConfirmationMismatchException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.PasswordSameAsCurrentException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TemporaryPasswordExpiredException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.DishAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.DishAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeCancelledException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidOrderStatusTransitionException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeInvoicedException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeModifiedException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableNotAvailableException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InsufficientStockException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.EmptyOrderException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.MenuAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.MenuAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.MenuNameAlreadyExistsException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableNumberAlreadyExistsException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableOccupiedException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.UserAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.UserAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidEmail(HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "INVALID_EMAIL",
                        "El correo electronico ingresado es invalido.",
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPassword(InvalidPasswordException error, HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "INVALID_PASSWORD",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException error,
                                                          HttpServletRequest request) {
        String message = error.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ":" + err.getDefaultMessage())
                .findFirst()
                .orElse("Datos inválidos");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "VALIDATION_ERROR",
                        message,
                        request.getRequestURI(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException error, HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        "NOT_FOUND",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException error, HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        "UNAUTHORIZED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(org.springframework.security.authentication.DisabledException.class)
    public ResponseEntity<?> handleDisabled(org.springframework.security.authentication.DisabledException error, HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        "USER_DISABLED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(TemporaryPasswordExpiredException.class)
    public ResponseEntity<?> handleTemporaryPasswordExpired(TemporaryPasswordExpiredException error,
                                                            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        "TEMPORARY_PASSWORD_EXPIRED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<?> handleInvalidCurrentPassword(InvalidCurrentPasswordException error,
                                                          HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        "INVALID_CURRENT_PASSWORD",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(PasswordSameAsCurrentException.class)
    public ResponseEntity<?> handlePasswordSameAsCurrent(PasswordSameAsCurrentException error,
                                                         HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "PASSWORD_SAME_AS_CURRENT",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(PasswordConfirmationMismatchException.class)
    public ResponseEntity<?> handlePasswordConfirmationMismatch(PasswordConfirmationMismatchException error,
                                                                HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "PASSWORD_CONFIRMATION_MISMATCH",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(TableNumberAlreadyExistsException.class)
    public ResponseEntity<?> handleTableNumberAlreadyExists(TableNumberAlreadyExistsException error,
                                                            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "TABLE_NUMBER_ALREADY_EXISTS",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MenuNameAlreadyExistsException.class)
    public ResponseEntity<?> handleMenuNameAlreadyExists(MenuNameAlreadyExistsException error,
                                                            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "MENU_NAME_ALREADY_EXISTS",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MenuAlreadyInactiveException.class)
    public ResponseEntity<?> handleMenuAlreadyInactive(MenuAlreadyInactiveException error,
                                                            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "MENU_ALREADY_INACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MenuAlreadyActiveException.class)
    public ResponseEntity<?> handleMenuAlreadyActive(MenuAlreadyActiveException error,
                                                            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "MENU_ALREADY_ACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(DishAlreadyInactiveException.class)
    public ResponseEntity<?> handleDishAlreadyInactive(DishAlreadyInactiveException error,
                                                       HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "DISH_ALREADY_INACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(DishAlreadyActiveException.class)
    public ResponseEntity<?> handleDishAlreadyActive(DishAlreadyActiveException error,
                                                     HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "DISH_ALREADY_ACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(OrderCannotBeCancelledException.class)
    public ResponseEntity<?> handleOrderCannotBeCancelled(OrderCannotBeCancelledException error,
                                                          HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "ORDER_CANNOT_BE_CANCELLED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ResponseEntity<?> handleInvalidOrderStatusTransition(InvalidOrderStatusTransitionException error,
                                                                HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "INVALID_ORDER_STATUS_TRANSITION",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(OrderCannotBeInvoicedException.class)
    public ResponseEntity<?> handleOrderCannotBeInvoiced(OrderCannotBeInvoicedException error,
                                                          HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "ORDER_CANNOT_BE_INVOICED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(TableAlreadyActiveException.class)
    public ResponseEntity<?> handleTableAlreadyActive(TableAlreadyActiveException error,
                                                      HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "TABLE_ALREADY_ACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(TableAlreadyInactiveException.class)
    public ResponseEntity<?> handleTableAlreadyInactive(TableAlreadyInactiveException error,
                                                        HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "TABLE_ALREADY_INACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(TableOccupiedException.class)
    public ResponseEntity<?> handleTableOccupied(TableOccupiedException error,
                                                 HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "TABLE_OCCUPIED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<?> handleInvalidField(InvalidFieldException error,
                                                HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "INVALID_FIELD",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(TableNotAvailableException.class)
    public ResponseEntity<?> handleTableNotAvailable(TableNotAvailableException error,
                                                     HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "TABLE_NOT_AVAILABLE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStock(InsufficientStockException error,
                                                     HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "INSUFFICIENT_STOCK",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<?> handleEmptyOrder(EmptyOrderException error,
                                              HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "EMPTY_ORDER",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(OrderCannotBeModifiedException.class)
    public ResponseEntity<?> handleOrderCannotBeModified(OrderCannotBeModifiedException error,
                                                         HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "ORDER_CANNOT_BE_MODIFIED",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserAlreadyActiveException.class)
    public ResponseEntity<?> handleUserAlreadyActive(UserAlreadyActiveException error,
                                                     HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "USER_ALREADY_ACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ResponseEntity<?> handleUserAlreadyInactive(UserAlreadyInactiveException error,
                                                       HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        "USER_ALREADY_INACTIVE",
                        error.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }
}
