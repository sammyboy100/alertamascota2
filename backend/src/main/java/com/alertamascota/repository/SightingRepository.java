package com.alertamascota.repository;

import com.alertamascota.model.Sighting;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SightingRepository extends MongoRepository<Sighting, String> {
    List<Sighting> findByAlertId(String alertId);
}
