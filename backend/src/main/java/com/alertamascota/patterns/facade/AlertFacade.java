package com.alertamascota.patterns.facade;

import com.alertamascota.model.Alert;
import com.alertamascota.model.Sighting;
import com.alertamascota.patterns.AlertValidationHandler;
import com.alertamascota.patterns.builder.AlertBuilder;
import com.alertamascota.repository.AlertRepository;
import com.alertamascota.repository.SightingRepository;
import com.alertamascota.service.observer.AlertObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PATRÓN ESTRUCTURAL: FACADE
 * Expone una interfaz simple al controller ocultando:
 * - Builder (construcción de alerta)
 * - Chain of Responsibility (validación)
 * - Observer (notificación)
 * - Repository (persistencia)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertFacade {

    private final AlertRepository alertRepository;
    private final SightingRepository sightingRepository;
    private final List<AlertObserver> observers;

    private final AlertValidationHandler validationChain = AlertValidationHandler.buildChain();

    /**
     * RF 1.1 + RF 1.2: Registrar alerta completa.
     */
    public Alert createAlert(Map<String, Object> body) {
        // BUILDER — construye el objeto paso a paso
        Alert alert = new AlertBuilder()
                .petName((String) body.get("petName"))
                .species((String) body.get("species"))
                .breed((String) body.getOrDefault("breed", ""))
                .description((String) body.getOrDefault("description", ""))
                .color((String) body.getOrDefault("color", ""))
                .location(
                    body.get("lat") != null ? Double.valueOf(body.get("lat").toString()) : null,
                    body.get("lng") != null ? Double.valueOf(body.get("lng").toString()) : null,
                    (String) body.getOrDefault("locationRef", "")
                )
                .owner(
                    (String) body.getOrDefault("ownerName", ""),
                    (String) body.getOrDefault("ownerPhone", "")
                )
                .build();

        // CHAIN OF RESPONSIBILITY — valida
        validationChain.validate(alert);

        // Persistir
        Alert saved = alertRepository.save(alert);

        // OBSERVER — notifica a suscriptores (RF 1.4)
        observers.forEach(o -> o.onAlertPublished(saved));

        return sanitize(saved);
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByStatus("ACTIVE").stream()
                .map(this::sanitize).toList();
    }

    public Alert resolveAlert(String id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alert.setStatus("RESOLVED");
        return alertRepository.save(alert);
    }

    /** RF 1.3 — avistamiento anónimo */
    public Sighting addSighting(Map<String, Object> body) {
        Sighting s = Sighting.builder()
                .alertId((String) body.get("alertId"))
                .description((String) body.getOrDefault("description", ""))
                .lat(body.get("lat") != null ? Double.valueOf(body.get("lat").toString()) : null)
                .lng(body.get("lng") != null ? Double.valueOf(body.get("lng").toString()) : null)
                .locationRef((String) body.getOrDefault("locationRef", ""))
                .build();
        return sightingRepository.save(s);
    }

    public List<Sighting> getSightings(String alertId) {
        return sightingRepository.findByAlertId(alertId);
    }

    /** RNF 1.2 — eliminar datos privados del dueño */
    private Alert sanitize(Alert alert) {
        alert.setOwnerPhone(null);
        return alert;
    }
}
