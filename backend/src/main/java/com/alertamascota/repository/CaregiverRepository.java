package com.alertamascota.repository;

import com.alertamascota.model.Caregiver;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CaregiverRepository extends MongoRepository<Caregiver, String> {
    List<Caregiver> findByVerifiedTrue();
    List<Caregiver> findByRole(String role);
}
