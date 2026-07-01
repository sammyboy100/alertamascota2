package com.alertamascota.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Avistamiento anónimo — RF 1.3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "avistamientos")
public class Sighting {

    @Id
    private String id;

    private String alertId;
    private String description;
    private Double lat;
    private Double lng;
    private String locationRef;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
