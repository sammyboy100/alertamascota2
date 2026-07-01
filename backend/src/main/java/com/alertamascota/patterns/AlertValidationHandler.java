package com.alertamascota.patterns;

import com.alertamascota.model.Alert;

/**
 * PATRÓN COMPORTAMIENTO: CHAIN OF RESPONSIBILITY
 * Valida una alerta pasándola por una cadena de handlers.
 * Cada handler valida un aspecto distinto (RF 1.1, 1.2).
 */
public abstract class AlertValidationHandler {

    protected AlertValidationHandler next;

    public AlertValidationHandler setNext(AlertValidationHandler next) {
        this.next = next;
        return next;
    }

    public abstract void validate(Alert alert);

    // ── Handler: campos obligatorios ─────────────────────────
    public static class RequiredFieldsHandler extends AlertValidationHandler {
        @Override
        public void validate(Alert alert) {
            if (alert.getPetName() == null || alert.getPetName().isBlank())
                throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
            if (alert.getSpecies() == null || alert.getSpecies().isBlank())
                throw new IllegalArgumentException("La especie es obligatoria.");
            if (next != null) next.validate(alert);
        }
    }

    // ── Handler: ubicación ────────────────────────────────────
    public static class LocationHandler extends AlertValidationHandler {
        @Override
        public void validate(Alert alert) {
            if (alert.getLat() == null || alert.getLng() == null)
                throw new IllegalArgumentException("La ubicación (lat/lng) es obligatoria — RF 1.2.");
            if (next != null) next.validate(alert);
        }
    }

    // ── Handler: datos del dueño ──────────────────────────────
    public static class OwnerHandler extends AlertValidationHandler {
        @Override
        public void validate(Alert alert) {
            if (alert.getOwnerPhone() == null || alert.getOwnerPhone().isBlank())
                throw new IllegalArgumentException("El teléfono de contacto es obligatorio.");
            if (next != null) next.validate(alert);
        }
    }

    /** Construye la cadena completa */
    public static AlertValidationHandler buildChain() {
        AlertValidationHandler fields   = new RequiredFieldsHandler();
        AlertValidationHandler location = new LocationHandler();
        AlertValidationHandler owner    = new OwnerHandler();
        fields.setNext(location).setNext(owner);
        return fields;
    }
}
