package com.alertamascota.service.observer;

import com.alertamascota.model.Alert;

/**
 * PATRÓN COMPORTAMIENTO: OBSERVER
 * Define la interfaz para objetos que quieren ser notificados
 * cuando se publica una nueva alerta (RF 1.4).
 */
public interface AlertObserver {
    void onAlertPublished(Alert alert);
}
