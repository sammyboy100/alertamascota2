package com.alertamascota.service.strategy;

import com.alertamascota.model.Alert;
import com.alertamascota.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

/** Busca por especie — RF 2.3 */
@Component
@RequiredArgsConstructor
class BySpeciesStrategy implements SearchStrategy {
    private final AlertRepository repo;

    @Override
    public List<Alert> search(Map<String, String> criteria) {
        String species = criteria.getOrDefault("species", "");
        return repo.findBySpeciesAndStatus(species, "ACTIVE");
    }

    @Override public String getType() { return "species"; }
}

/** Busca por raza — RF 2.4 */
@Component
@RequiredArgsConstructor
class ByBreedStrategy implements SearchStrategy {
    private final AlertRepository repo;

    @Override
    public List<Alert> search(Map<String, String> criteria) {
        String breed = criteria.getOrDefault("breed", "");
        return repo.findByBreedContainingIgnoreCaseAndStatus(breed, "ACTIVE");
    }

    @Override public String getType() { return "breed"; }
}

/** Devuelve todas las activas por defecto */
@Component
@RequiredArgsConstructor
class AllActiveStrategy implements SearchStrategy {
    private final AlertRepository repo;

    @Override
    public List<Alert> search(Map<String, String> criteria) {
        return repo.findByStatus("ACTIVE");
    }

    @Override public String getType() { return "all"; }
}
