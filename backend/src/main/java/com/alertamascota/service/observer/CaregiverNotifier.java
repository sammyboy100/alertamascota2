package com.alertamascota.service.observer;

import com.alertamascota.model.Alert;
import com.alertamascota.model.Caregiver;
import com.alertamascota.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Observer concreto: notifica a cuidadores con alertsEnabled=true (RF 3.3).
 * En producción enviaría email/push; aquí loguea.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaregiverNotifier implements AlertObserver {

    private final CaregiverRepository caregiverRepository;

    @Override
    public void onAlertPublished(Alert alert) {
        List<Caregiver> toNotify = caregiverRepository.findByVerifiedTrue()
                .stream()
                .filter(Caregiver::isAlertsEnabled)
                .toList();

        log.info("[Observer] Nueva alerta '{}' — notificando {} cuidador(es)",
                alert.getPetName(), toNotify.size());

        toNotify.forEach(c ->
            log.info("[Observer] → Notificado: {} ({})", c.getName(), c.getRole())
        );
    }
}
