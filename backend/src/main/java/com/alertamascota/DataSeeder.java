package com.alertamascota;

import com.alertamascota.model.Alert;
import com.alertamascota.model.Caregiver;
import com.alertamascota.repository.AlertRepository;
import com.alertamascota.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AlertRepository alertRepo;
    private final CaregiverRepository caregiverRepo;

    @Override
    public void run(String... args) {
        if (alertRepo.count() == 0) {
            alertRepo.saveAll(List.of(
                Alert.builder().petName("Luna").species("Perro").breed("Labrador")
                    .description("Collar azul, muy amigable").color("Dorado")
                    .lat(-12.0464).lng(-77.0428).locationRef("Miraflores, Lima")
                    .ownerName("Ana García").ownerPhone("987654321").status("ACTIVE").build(),
                Alert.builder().petName("Max").species("Gato").breed("Siamés")
                    .description("Ojos azules, tímido").color("Blanco y gris")
                    .lat(-12.051).lng(-77.044).locationRef("San Isidro, Lima")
                    .ownerName("Carlos Ruiz").ownerPhone("912345678").status("ACTIVE").build(),
                Alert.builder().petName("Coco").species("Perro").breed("Beagle")
                    .description("Collar rojo, muy juguetón").color("Tricolor")
                    .lat(-12.055).lng(-77.035).locationRef("Barranco, Lima")
                    .ownerName("María López").ownerPhone("999111222").status("ACTIVE").build()
            ));
            log.info("[Seeder] Alertas de demo insertadas");
        }

        if (caregiverRepo.count() == 0) {
            caregiverRepo.saveAll(List.of(
                Caregiver.builder().name("María López").role("PROFESIONAL")
                    .bio("Veterinaria con 5 años de experiencia").district("Miraflores")
                    .species(List.of("Perro","Gato")).sizes(List.of("Pequeño","Mediano"))
                    .alertsEnabled(true).verified(true).rating(4.8).reviewCount(23).build(),
                Caregiver.builder().name("Pedro Salas").role("SOLIDARIO")
                    .bio("Amante de los animales, disponible fines de semana").district("San Isidro")
                    .species(List.of("Perro")).sizes(List.of("Pequeño"))
                    .alertsEnabled(false).verified(true).rating(4.3).reviewCount(8).build()
            ));
            log.info("[Seeder] Cuidadores de demo insertados");
        }
    }
}
