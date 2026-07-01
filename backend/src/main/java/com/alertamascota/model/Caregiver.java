package com.alertamascota.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Modelo de Cuidador.
 * RF 3.1 — roles: SOLIDARIO, PROFESIONAL, ESPECIALIZADO
 * RF 3.2 — restricciones de servicio
 * RF 3.3 — toggle de alertas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cuidadores")
public class Caregiver {

    @Id
    private String id;

    private String name;
    private String role;           // SOLIDARIO, PROFESIONAL, ESPECIALIZADO
    private List<String> species;  // RF 3.2
    private List<String> sizes;    // RF 3.2

    @Builder.Default
    private boolean alertsEnabled = false; // RF 3.3

    @Builder.Default
    private boolean verified = false;      // RNF 3.1

    private double rating;
    private int reviewCount;
    private String bio;
    private String district;
}
