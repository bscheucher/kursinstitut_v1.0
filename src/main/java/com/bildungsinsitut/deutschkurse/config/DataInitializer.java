package com.bildungsinsitut.deutschkurse.config;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.enums.Role;
import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import com.bildungsinsitut.deutschkurse.model.*;
import com.bildungsinsitut.deutschkurse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!prod") // Don't run in production
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            AbteilungRepository abteilungRepository,
            KursraumRepository kursraumRepository,
            KurstypRepository kurstypRepository,
            TrainerRepository trainerRepository,
            KursRepository kursRepository) {

        return args -> {
            // Check if data already exists - check multiple tables to be safe
            if (userRepository.count() > 0 || abteilungRepository.count() > 0 || kurstypRepository.count() > 0) {
                log.info("Database already contains data, skipping initialization");
                return;
            }

            log.info("Initializing database with sample data...");

            try {
                // Create default admin user
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@deutschkurse.de");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setFirstName("System");
                adminUser.setLastName("Administrator");
                adminUser.setRole(Role.ADMIN);
                adminUser.setEnabled(true);
                adminUser.setAccountNonExpired(true);
                adminUser.setAccountNonLocked(true);
                adminUser.setCredentialsNonExpired(true);
                userRepository.save(adminUser);
                log.info("Created admin user: admin / admin123");

                // Create trainer user
                User trainerUser = new User();
                trainerUser.setUsername("maria.schmidt");
                trainerUser.setEmail("maria.schmidt@deutschkurse.de");
                trainerUser.setPassword(passwordEncoder.encode("trainer123"));
                trainerUser.setFirstName("Maria");
                trainerUser.setLastName("Schmidt");
                trainerUser.setRole(Role.TRAINER);
                trainerUser.setEnabled(true);
                trainerUser.setAccountNonExpired(true);
                trainerUser.setAccountNonLocked(true);
                trainerUser.setCredentialsNonExpired(true);
                userRepository.save(trainerUser);
                log.info("Created trainer user: maria.schmidt / trainer123");

                // Create staff user
                User staffUser = new User();
                staffUser.setUsername("staff");
                staffUser.setEmail("staff@deutschkurse.de");
                staffUser.setPassword(passwordEncoder.encode("staff123"));
                staffUser.setFirstName("Staff");
                staffUser.setLastName("Member");
                staffUser.setRole(Role.STAFF);
                staffUser.setEnabled(true);
                staffUser.setAccountNonExpired(true);
                staffUser.setAccountNonLocked(true);
                staffUser.setCredentialsNonExpired(true);
                userRepository.save(staffUser);
                log.info("Created staff user: staff / staff123");

                // Create regular user
                User regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setEmail("user@deutschkurse.de");
                regularUser.setPassword(passwordEncoder.encode("user123"));
                regularUser.setFirstName("Test");
                regularUser.setLastName("User");
                regularUser.setRole(Role.USER);
                regularUser.setEnabled(true);
                regularUser.setAccountNonExpired(true);
                regularUser.setAccountNonLocked(true);
                regularUser.setCredentialsNonExpired(true);
                userRepository.save(regularUser);
                log.info("Created regular user: user / user123");

                // Create Abteilungen - Check if it doesn't already exist
                Abteilung hauptgebaeude = abteilungRepository.findByAbteilungName("Hauptgebäude");
                if (hauptgebaeude == null) {
                    hauptgebaeude = new Abteilung();
                    hauptgebaeude.setAbteilungName("Hauptgebäude");
                    hauptgebaeude.setBeschreibung("Zentrale Verwaltung und Standardkurse");
                    hauptgebaeude.setAktiv(true);
                    hauptgebaeude = abteilungRepository.save(hauptgebaeude);
                    log.info("Created Abteilung: Hauptgebäude");
                }

                // Create Kursräume
                if (kursraumRepository.count() == 0) {
                    Kursraum raum1 = new Kursraum();
                    raum1.setAbteilung(hauptgebaeude);
                    raum1.setRaumName("Raum A101");
                    raum1.setKapazitaet(12);
                    raum1.setAusstattung("Whiteboard, Beamer, Flipchart");
                    raum1.setVerfuegbar(true);
                    kursraumRepository.save(raum1);
                    log.info("Created Kursraum: Raum A101");
                }

                // Create Kurstypen - Check if it doesn't already exist
                Kurstyp a1 = kurstypRepository.findByKurstypCode("A1");
                if (a1 == null) {
                    a1 = new Kurstyp();
                    a1.setKurstypCode("A1");
                    a1.setKurstypName("Deutsch A1");
                    a1.setBeschreibung("Grundstufe - Erste Kenntnisse");
                    a1.setLevelOrder(1);
                    a1.setAktiv(true);
                    a1 = kurstypRepository.save(a1);
                    log.info("Created Kurstyp: A1");
                }

                // Create Trainer linked to user
                if (trainerRepository.count() == 0) {
                    Trainer trainer1 = new Trainer();
                    trainer1.setVorname("Maria");
                    trainer1.setNachname("Schmidt");
                    trainer1.setEmail("maria.schmidt@deutschkurse.de");
                    trainer1.setTelefon("030-12345-01");
                    trainer1.setAbteilung(hauptgebaeude);
                    trainer1.setStatus(TrainerStatus.verfuegbar);
                    trainer1.setQualifikationen("DaF/DaZ Zertifikat, 5 Jahre Erfahrung");
                    trainer1.setEinstellungsdatum(LocalDate.now().minusYears(2));
                    trainer1.setAktiv(true);
                    trainer1.setUser(trainerUser); // Link to user account
                    trainer1 = trainerRepository.save(trainer1);
                    log.info("Created Trainer: Maria Schmidt");

                    // Create Kurs only if we have all dependencies and no existing courses
                    if (kursRepository.count() == 0) {
                        Kursraum raum = kursraumRepository.findAll().get(0); // Get the first room

                        Kurs kurs1 = new Kurs();
                        kurs1.setKursName("Deutsch A1 - Anfänger Morgens");
                        kurs1.setKurstyp(a1);
                        kurs1.setKursraum(raum);
                        kurs1.setTrainer(trainer1);
                        kurs1.setStartdatum(LocalDate.now().plusDays(7));
                        kurs1.setEnddatum(LocalDate.now().plusMonths(3));
                        kurs1.setMaxTeilnehmer(12);
                        kurs1.setAktuelleTeilnehmer(0);
                        kurs1.setStatus(KursStatusType.geplant);
                        kurs1.setBeschreibung("Deutschkurs für absolute Anfänger");
                        kursRepository.save(kurs1);
                        log.info("Created Kurs: Deutsch A1 - Anfänger Morgens");
                    }
                }

                log.info("Sample data initialization completed successfully");
                log.info("=== Default Users Created ===");
                log.info("Admin: admin / admin123 (ADMIN role)");
                log.info("Trainer: maria.schmidt / trainer123 (TRAINER role)");
                log.info("Staff: staff / staff123 (STAFF role)");
                log.info("User: user / user123 (USER role)");

            } catch (Exception e) {
                log.error("Error initializing sample data: {}", e.getMessage(), e);
            }
        };
    }
}