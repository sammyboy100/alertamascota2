package com.alertamascota.service;

import com.alertamascota.model.Caregiver;
import com.alertamascota.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CaregiverService {

    private final CaregiverRepository repo;

    public List<Caregiver> getVerified() {
        return repo.findByVerifiedTrue();
    }

    public Caregiver register(Map<String, Object> body) {
        Caregiver c = Caregiver.builder()
                .name((String) body.get("name"))
                .role((String) body.getOrDefault("role", "SOLIDARIO"))
                .bio((String) body.getOrDefault("bio", ""))
                .district((String) body.getOrDefault("district", ""))
                .species(body.get("species") != null ? (List<String>) body.get("species") : List.of())
                .sizes(body.get("sizes") != null ? (List<String>) body.get("sizes") : List.of())
                .alertsEnabled(false)
                .verified(false)
                .build();
        return repo.save(c);
    }

    /** RF 3.3 — toggle de alertas */
    public Caregiver toggleAlerts(String id, boolean enabled) {
        Caregiver c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuidador no encontrado"));
        c.setAlertsEnabled(enabled);
        return repo.save(c);
    }

    /** RNF 3.1 — verificar identidad */
    public Caregiver verify(String id) {
        Caregiver c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuidador no encontrado"));
        c.setVerified(true);
        return repo.save(c);
    }

    public List<Caregiver> getAll() {
        return repo.findAll();
    }
}
