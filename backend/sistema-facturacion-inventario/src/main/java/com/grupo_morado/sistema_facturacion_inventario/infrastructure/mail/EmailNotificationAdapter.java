package com.grupo_morado.sistema_facturacion_inventario.infrastructure.mail;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.EmailNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Adaptador de infraestructura para el envío de correos electrónicos.
 * Implementa {@link EmailNotificationPort} usando {@link JavaMailSender}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements EmailNotificationPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}")
    private String resendFromEmail;

    @Override
    public void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword) {
        if (resendApiKey != null && !resendApiKey.isBlank()) {
            sendEmailViaResendApi(toEmail, temporaryPassword);
        } else {
            sendEmailViaSmtp(toEmail, temporaryPassword);
        }
    }

    private void sendEmailViaResendApi(String toEmail, String temporaryPassword) {
        log.info("Iniciando envío de correo a {} usando la API HTTP de Resend...", toEmail);
        try {
            String htmlBody = buildEmailBody(temporaryPassword);
            String escapedHtml = htmlBody
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String json = "{"
                    + "\"from\":\"" + resendFromEmail + "\","
                    + "\"to\":\"" + toEmail + "\","
                    + "\"subject\":\"Recuperación de contraseña — Sistema de Facturación\","
                    + "\"html\":\"" + escapedHtml + "\""
                    + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Correo de recuperación enviado con éxito a {} mediante la API de Resend.", toEmail);
            } else {
                log.error("Fallo al enviar correo mediante la API de Resend. Código: {}, Cuerpo: {}", 
                        response.statusCode(), response.body());
                throw new RuntimeException("Fallo al enviar correo mediante la API de Resend: " + response.body());
            }
        } catch (Exception e) {
            log.error("Error al enviar correo de recuperación vía Resend a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperación de contraseña vía Resend.", e);
        }
    }

    private void sendEmailViaSmtp(String toEmail, String temporaryPassword) {
        log.info("Iniciando envío de correo a {} usando SMTP de Gmail...", toEmail);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Recuperación de contraseña — Sistema de Facturación");
            helper.setText(buildEmailBody(temporaryPassword), true);

            mailSender.send(message);
            log.info("Correo de recuperación de contraseña enviado exitosamente a: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Error al enviar correo de recuperación a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperación de contraseña.", e);
        }
    }

    /**
     * Construye el cuerpo HTML del correo de recuperación de contraseña.
     *
     * @param temporaryPassword Contraseña temporal en texto plano.
     * @return Cadena HTML del correo.
     */
    private String buildEmailBody(String temporaryPassword) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0; padding:0; background-color:#f4f4f7; font-family: 'Segoe UI', Arial, sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f7; padding: 40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background-color:#ffffff; border-radius:8px;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.08); overflow:hidden;">

                          <!-- Encabezado -->
                          <tr>
                            <td style="background-color:#4f46e5; padding: 32px 40px; text-align:center;">
                              <h1 style="color:#ffffff; margin:0; font-size:22px; font-weight:700; letter-spacing:0.5px;">
                                Sistema de Facturación y Restaurante
                              </h1>
                            </td>
                          </tr>

                          <!-- Cuerpo -->
                          <tr>
                            <td style="padding: 40px 40px 24px;">
                              <h2 style="color:#1a1a2e; font-size:20px; margin:0 0 16px;">
                                Recuperación de contraseña
                              </h2>
                              <p style="color:#4a4a68; font-size:15px; line-height:1.7; margin:0 0 24px;">
                                Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                                A continuación te proporcionamos una <strong>contraseña temporal</strong>
                                que podrás usar para iniciar sesión.
                              </p>

                              <!-- Contraseña temporal -->
                              <table width="100%%" cellpadding="0" cellspacing="0"
                                     style="margin: 0 0 24px;">
                                <tr>
                                  <td style="background-color:#f0f0ff; border-left:4px solid #4f46e5;
                                             border-radius:4px; padding:20px 24px; text-align:center;">
                                    <p style="color:#4a4a68; font-size:13px; margin:0 0 8px;
                                              text-transform:uppercase; letter-spacing:1px;">
                                      Tu contraseña temporal es:
                                    </p>
                                    <p style="color:#1a1a2e; font-size:26px; font-weight:700;
                                              font-family:monospace; letter-spacing:3px; margin:0;">
                                """ + temporaryPassword + """
                                    </p>
                                  </td>
                                </tr>
                              </table>

                              <!-- Aviso de expiración -->
                              <table width="100%%" cellpadding="0" cellspacing="0"
                                     style="margin: 0 0 28px;">
                                <tr>
                                  <td style="background-color:#fff8e1; border-left:4px solid #f59e0b;
                                             border-radius:4px; padding:14px 20px;">
                                    <p style="color:#78350f; font-size:14px; margin:0;">
                                      ⏱ <strong>Esta contraseña expirará en exactamente 5 minutos.</strong>
                                      Si no inicias sesión dentro de ese tiempo, deberás solicitar
                                      un nuevo restablecimiento.
                                    </p>
                                  </td>
                                </tr>
                              </table>

                              <p style="color:#4a4a68; font-size:15px; line-height:1.7; margin:0 0 12px;">
                                Una vez que inicies sesión con esta contraseña temporal,
                                el sistema te pedirá que establezcas una nueva contraseña definitiva
                                antes de continuar.
                              </p>
                              <p style="color:#4a4a68; font-size:14px; line-height:1.7; margin:0;">
                                Si no solicitaste este cambio, ignora este correo.
                                Tu contraseña actual permanece sin cambios.
                              </p>
                            </td>
                          </tr>

                          <!-- Pie de página -->
                          <tr>
                            <td style="background-color:#f4f4f7; padding:24px 40px; text-align:center;
                                        border-top:1px solid #e5e7eb;">
                              <p style="color:#9ca3af; font-size:12px; margin:0;">
                                Este es un correo automático, por favor no respondas a este mensaje.
                              </p>
                              <p style="color:#9ca3af; font-size:12px; margin:6px 0 0;">
                                &copy; Sistema de Facturación y Restaurante
                              </p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """;
    }
}
