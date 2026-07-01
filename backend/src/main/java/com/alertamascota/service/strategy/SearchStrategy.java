package com.alertamascota.service.strategy;

import com.alertamascota.model.Alert;
import java.util.List;
import java.util.Map;

/**
 * PATRÓN COMPORTAMIENTO: STRATEGY
 * Interfaz para los distintos algoritmos de búsqueda (RF 2.2-2.5).
 * Permite intercambiar el motor de búsqueda sin cambiar el contexto.
 */
public interface SearchStrategy {
    List<Alert> search(Map<String, String> criteria);
    String getType();
}
