package com.alertamascota.patterns.builder;

import com.alertamascota.model.Alert;

/**
 * PATRÓN CREACIONAL: BUILDER
 * Construye objetos Alert paso a paso validando cada campo.
 * Permite crear alertas complejas sin constructores enormes.
 */
public class AlertBuilder {

    private String petName;
    private String species;
    private String breed;
    private String description;
    private String color;
    private Double lat;
    private Double lng;
    private String locationRef;
    private String ownerName;
    private String ownerPhone;

    public AlertBuilder petName(String petName) {
        if (petName == null || petName.isBlank())
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        this.petName = petName;
        return this;
    }

    public AlertBuilder species(String species) {
        this.species = species;
        return this;
    }

    public AlertBuilder breed(String breed) {
        this.breed = breed;
        return this;
    }

    public AlertBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AlertBuilder color(String color) {
        this.color = color;
        return this;
    }

    public AlertBuilder location(Double lat, Double lng, String ref) {
        this.lat = lat;
        this.lng = lng;
        this.locationRef = ref;
        return this;
    }

    public AlertBuilder owner(String name, String phone) {
        this.ownerName = name;
        this.ownerPhone = phone;
        return this;
    }

    public Alert build() {
        return Alert.builder()
                .petName(petName)
                .species(species)
                .breed(breed)
                .description(description)
                .color(color)
                .lat(lat)
                .lng(lng)
                .locationRef(locationRef)
                .ownerName(ownerName)
                .ownerPhone(ownerPhone)
                .status("ACTIVE")
                .build();
    }
}
