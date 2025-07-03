package com.bildungsinsitut.deutschkurse.config;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import com.bildungsinsitut.deutschkurse.model.*;
import com.bildungsinsitut.deutschkurse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!prod") // Don't run in production
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            AbteilungRepository abteilungRepository,
            KursraumRepository kursraumRepository,
            KurstypRepository kurstypRepository,
            TrainerRepository trainerRepository,
            KursRepository kursRepository) {

        return args -> {
            // Check if data already exists
            if (abteilungRepository.count() > 0) {
                log.info("Database already contains data, skipping initialization");
                return;
            }

            log.info("Initializing database with sample data...");

            // Create sample data only if tables are empty
            try {
                // Create Abteilungen
                Abteilung hauptgebaeude = new Abteilung();
                hauptgebaeude.setAbteilungName("Hauptgebäude");
                hauptgebaeude.setBeschreibung("Zentrale Verwaltung und Standardkurse");
                hauptgebaeude.setAktiv(true);
                abteilungRepository.save(hauptgebaeude);

                // Create Kursräume
                Kursraum raum1 = new Kursraum();
                raum1.setAbteilung(hauptgebaeude);
                raum1.setRaumName("Raum A101");
                raum1.setKapazitaet(12);
                raum1.setAusstattung("Whiteboard, Beamer, Flipchart");
                raum1.setVerfuegbar(true);
                kursraumRepository.save(raum1);

                // Create Kurstypen
                Kurstyp a1 = new Kurstyp();
                a1.setKurstypCode("A1");
                a1.setKurstypName("Deutsch A1");
                a1.setBeschreibung("Grundstufe - Erste Kenntnisse");
                a1.setLevelOrder(1);
                a1.setAktiv(true);
                kurstypRepository.save(a1);

                // Create Trainer
                Trainer trainer1 = new Trainer();
                trainer1.setVorname("Maria");
                trainer1.setNachname("Schmidt");
                trainer1.setEmail("maria.schmidt@institut.de");
                trainer1.setTelefon("030-12345-01");
                trainer1.setAbteilung(hauptgebaeude);
                trainer1.setStatus(TrainerStatus.verfuegbar);
                trainer1.setQualifikationen("DaF/DaZ Zertifikat, 5 Jahre Erfahrung");
                trainer1.setEinstellungsdatum(LocalDate.now().minusYears(2));
                trainer1.setAktiv(true);
                trainerRepository.save(trainer1);

                // Create Kurs
                Kurs kurs1 = new Kurs();
                kurs1.setKursName("Deutsch A1 - Anfänger Morgens");
                kurs1.setKurstyp(a1);
                kurs1.setKursraum(raum1);
                kurs1.setTrainer(trainer1);
                kurs1.setStartdatum(LocalDate.now().plusDays(7));
                kurs1.setEnddatum(LocalDate.now().plusMonths(3));
                kurs1.setMaxTeilnehmer(12);
                kurs1.setAktuelleTeilnehmer(0);
                kurs1.setStatus(KursStatusType.geplant);
                kurs1.setBeschreibung("Deutschkurs für absolute Anfänger");
                kursRepository.save(kurs1);

                log.info("Sample data initialization completed");

            } catch (Exception e) {
                log.error("Error initializing sample data: {}", e.getMessage());
            }
        };
    }
}