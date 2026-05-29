package com.grupo_morado.sistema_facturacion_inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SistemaDeFacturacionYInventarioParaRestaurantesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDeFacturacionYInventarioParaRestaurantesApplication.class, args);
	}

}
