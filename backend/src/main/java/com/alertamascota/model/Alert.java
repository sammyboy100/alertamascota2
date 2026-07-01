package com.alertamascota.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Modelo de Alerta de mascota perdida.
 * RF 1.1 — nombre, especie, raza, descripción
 * RF 1.2 — coordenadas lat/lng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alertas")
public class Alert {

    @Id
    private String id;

    // RF 1.1
    private String petName;
    private String species;   // Perro, Gato, Ave, Conejo, Otro
    private String breed;
    private String description;
    private String color;

    // RF 1.2 — coordenadas geográficas
    private Double lat;
    private Double lng;
    private String locationRef;   // referencia textual del lugar

    // Estado: ACTIVE, SIGHTED, RESOLVED
    @Builder.Default
    private String status = "ACTIVE";

    // RNF 1.2 — datos del dueño (no expuestos en respuesta pública)
    private String ownerName;
    private String ownerPhone;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
