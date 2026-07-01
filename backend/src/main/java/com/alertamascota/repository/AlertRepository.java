package com.alertamascota.repository;

import com.alertamascota.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findByStatus(String status);
    List<Alert> findBySpeciesAndStatus(String species, String status);
    List<Alert> findByBreedContainingIgnoreCaseAndStatus(String breed, String status);
}
