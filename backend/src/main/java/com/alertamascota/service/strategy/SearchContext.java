package com.alertamascota.service.strategy;

import com.alertamascota.model.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * Contexto del patrón Strategy.
 * Selecciona la estrategia de búsqueda correcta según el criterio.
 */
@Service
@RequiredArgsConstructor
public class SearchContext {

    private final List<SearchStrategy> strategies;

    public List<Alert> search(String type, Map<String, String> criteria) {
        return strategies.stream()
                .filter(s -> s.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estrategia no encontrada: " + type))
                .search(criteria);
    }
}
