package com.bandsync.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class NotificacionService {

    private static final String FROM = "BandSync <onboarding@resend.dev>";
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String apiKey;

    public NotificacionService(@Value("${app.resend.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    private boolean modoSimulacion() {
        return apiKey == null || apiKey.isBlank()
                || apiKey.startsWith("${")
                || apiKey.equalsIgnoreCase("tu-api-key-aqui")
                || apiKey.startsWith("re_placeholder");
    }

    private void enviarEmail(String para, String asunto, String html) {
        if (modoSimulacion()) {
            log.warn("Resend no configurado, simulando envío");
            System.out.println("=== SIMULACIÓN EMAIL ===");
            System.out.println("Para:   " + para);
            System.out.println("Asunto: " + asunto);
            System.out.println("Body:   " + html);
            System.out.println("========================");
            return;
        }
        try {
            Resend resend = new Resend(apiKey);
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(FROM)
                    .to(para)
                    .subject(asunto)
                    .html(html)
                    .build();
            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email enviado a {} — id: {}", para, response.getId());
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", para, e.getMessage(), e);
        }
    }

    public void enviarRecordatorioEnsayo(String correoDestino, String nombreBanda,
                                          LocalDateTime fechaEnsayo, String direccionSala) {
        String html = """
                <h2>Recordatorio de Ensayo — %s</h2>
                <p>Tienes un ensayo programado:</p>
                <ul>
                  <li><strong>Banda:</strong> %s</li>
                  <li><strong>Fecha:</strong> %s</li>
                  <li><strong>Lugar:</strong> %s</li>
                </ul>
                <p>¡No faltes!</p>
                """.formatted(nombreBanda, nombreBanda, fechaEnsayo.format(FMT_DATETIME), direccionSala);
        enviarEmail(correoDestino, "Recordatorio de Ensayo — " + nombreBanda, html);
    }

    public void enviarRecordatorioShow(String correoDestino, String nombreEvento,
                                        String venue, LocalDateTime fecha) {
        String html = """
                <h2>Recordatorio de Show</h2>
                <p>Tu próximo show se acerca:</p>
                <ul>
                  <li><strong>Evento:</strong> %s</li>
                  <li><strong>Venue:</strong> %s</li>
                  <li><strong>Fecha:</strong> %s</li>
                </ul>
                <p>¡Mucha suerte en el escenario!</p>
                """.formatted(nombreEvento, venue, fecha.format(FMT_DATETIME));
        enviarEmail(correoDestino, "Recordatorio de Show — " + nombreEvento, html);
    }

    public void enviarAlertaVencimientoContrato(String correoDestino, String tipoContrato,
                                                 LocalDate fechaVencimiento) {
        String html = """
                <h2>Alerta: Contrato próximo a vencer</h2>
                <p>Uno de tus contratos está por vencer:</p>
                <ul>
                  <li><strong>Tipo:</strong> %s</li>
                  <li><strong>Vencimiento:</strong> %s</li>
                </ul>
                <p>Revisa el estado del contrato en BandSync antes de que expire.</p>
                """.formatted(tipoContrato, fechaVencimiento.format(FMT_DATE));
        enviarEmail(correoDestino, "Alerta: Contrato próximo a vencer", html);
    }

    public void enviarDetallePago(String correoDestino, String nombreArtista,
                                   Double montoNeto, String nombreShow) {
        String html = """
                <h2>Detalle de Pago</h2>
                <p>Hola <strong>%s</strong>, se ha registrado un nuevo pago:</p>
                <ul>
                  <li><strong>Show:</strong> %s</li>
                  <li><strong>Monto neto:</strong> $%.2f</li>
                </ul>
                <p>Gracias por tu trabajo con BandSync.</p>
                """.formatted(nombreArtista, nombreShow, montoNeto);
        enviarEmail(correoDestino, "Detalle de Pago — " + nombreShow, html);
    }

    public void enviarCodigoInvitacion(String correoDestino, String nombreBanda,
                                        String codigoInvitacion) {
        String html = """
                <h2>Invitación a unirte a %s</h2>
                <p>Has recibido una invitación para unirte a la banda <strong>%s</strong>.</p>
                <p>Usa el siguiente código en BandSync:</p>
                <h3 style="font-size:2em;letter-spacing:6px;font-family:monospace;">%s</h3>
                <p>El código es personal — no lo compartas.</p>
                """.formatted(nombreBanda, nombreBanda, codigoInvitacion);
        enviarEmail(correoDestino, "Invitación a unirte a " + nombreBanda, html);
    }

    public void enviarConfirmacionRegistro(String correoDestino, String nombre) {
        String html = """
                <h2>¡Bienvenido a BandSync, %s!</h2>
                <p>Tu cuenta ha sido creada exitosamente.</p>
                <p>Ya puedes acceder a la plataforma y comenzar a gestionar tu carrera musical.</p>
                <p>¡Mucho éxito!</p>
                """.formatted(nombre);
        enviarEmail(correoDestino, "Bienvenido a BandSync", html);
    }
}
