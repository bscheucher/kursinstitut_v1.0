-- =============================================================================
-- Simplified PostgreSQL Schema for German Language Courses
-- Core functionality only - removed enterprise features
-- =============================================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================================================
-- CORE TABLES
-- =============================================================================

-- Departments
CREATE TABLE abteilungen (
    abteilung_id SERIAL PRIMARY KEY,
    abteilung_name VARCHAR(100) NOT NULL,
    beschreibung TEXT,
    aktiv BOOLEAN DEFAULT TRUE,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Classrooms
CREATE TABLE kursraeume (
    kursraum_id SERIAL PRIMARY KEY,
    abteilung_id INTEGER NOT NULL,
    raum_name VARCHAR(50) NOT NULL,
    kapazitaet INTEGER DEFAULT 12 CHECK (kapazitaet <= 20),
    ausstattung TEXT,
    verfuegbar BOOLEAN DEFAULT TRUE,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (abteilung_id) REFERENCES abteilungen(abteilung_id),
    UNIQUE (abteilung_id, raum_name)
);

-- Course Types/Levels
CREATE TABLE kurstypen (
    kurstyp_id SERIAL PRIMARY KEY,
    kurstyp_code VARCHAR(20) NOT NULL UNIQUE,
    kurstyp_name VARCHAR(100) NOT NULL,
    beschreibung TEXT,
    level_order INTEGER, -- 1=A1, 2=A2, 3=B1, etc.
    aktiv BOOLEAN DEFAULT TRUE
);

-- Teachers
CREATE TABLE trainer (
    trainer_id SERIAL PRIMARY KEY,
    vorname VARCHAR(100) NOT NULL,
    nachname VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    telefon VARCHAR(20),
    abteilung_id INTEGER,
    status VARCHAR(20) DEFAULT 'verfuegbar', -- verfuegbar, im_einsatz, abwesend
    qualifikationen TEXT,
    einstellungsdatum DATE,
    aktiv BOOLEAN DEFAULT TRUE,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    geaendert_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (abteilung_id) REFERENCES abteilungen(abteilung_id)
);

-- Students
CREATE TABLE teilnehmer (
    teilnehmer_id SERIAL PRIMARY KEY,
    vorname VARCHAR(100) NOT NULL,
    nachname VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    telefon VARCHAR(20),
    geburtsdatum DATE,
    geschlecht CHAR(1) CHECK (geschlecht IN ('m', 'w', 'd')),
    staatsangehoerigkeit VARCHAR(100),
    muttersprache VARCHAR(100),
    anmeldedatum DATE DEFAULT CURRENT_DATE,
    aktiv BOOLEAN DEFAULT TRUE,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    geaendert_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses
CREATE TABLE kurse (
    kurs_id SERIAL PRIMARY KEY,
    kurs_name VARCHAR(200) NOT NULL,
    kurstyp_id INTEGER NOT NULL,
    kursraum_id INTEGER NOT NULL,
    trainer_id INTEGER NOT NULL,
    startdatum DATE NOT NULL,
    enddatum DATE,
    max_teilnehmer INTEGER DEFAULT 12 CHECK (max_teilnehmer <= 20),
    aktuelle_teilnehmer INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'geplant', -- geplant, laufend, abgeschlossen, abgebrochen
    beschreibung TEXT,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    geaendert_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (kurstyp_id) REFERENCES kurstypen(kurstyp_id),
    FOREIGN KEY (kursraum_id) REFERENCES kursraeume(kursraum_id),
    FOREIGN KEY (trainer_id) REFERENCES trainer(trainer_id)
);

-- Student-Course Assignments
CREATE TABLE teilnehmer_kurse (
    zuordnung_id SERIAL PRIMARY KEY,
    teilnehmer_id INTEGER NOT NULL,
    kurs_id INTEGER NOT NULL,
    anmeldedatum DATE DEFAULT CURRENT_DATE,
    abmeldedatum DATE,
    status VARCHAR(20) DEFAULT 'angemeldet', -- angemeldet, aktiv, abgeschlossen, abgebrochen
    abschlussnote DECIMAL(3,2),
    bemerkungen TEXT,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    geaendert_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teilnehmer_id) REFERENCES teilnehmer(teilnehmer_id),
    FOREIGN KEY (kurs_id) REFERENCES kurse(kurs_id) ON DELETE CASCADE,
    UNIQUE (teilnehmer_id, kurs_id)
);

-- Course Schedule
CREATE TABLE stundenplan (
    stundenplan_id SERIAL PRIMARY KEY,
    kurs_id INTEGER NOT NULL,
    wochentag VARCHAR(10) NOT NULL, -- Montag, Dienstag, etc.
    startzeit TIME NOT NULL,
    endzeit TIME NOT NULL,
    bemerkungen TEXT,
    aktiv BOOLEAN DEFAULT TRUE,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (kurs_id) REFERENCES kurse(kurs_id) ON DELETE CASCADE,
    UNIQUE (kurs_id, wochentag, startzeit)
);

-- Attendance (simplified)
CREATE TABLE anwesenheit (
    anwesenheit_id SERIAL PRIMARY KEY,
    teilnehmer_id INTEGER NOT NULL,
    kurs_id INTEGER NOT NULL,
    datum DATE NOT NULL,
    anwesend BOOLEAN DEFAULT TRUE,
    entschuldigt BOOLEAN DEFAULT FALSE,
    bemerkung TEXT,
    erfasst_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teilnehmer_id) REFERENCES teilnehmer(teilnehmer_id),
    FOREIGN KEY (kurs_id) REFERENCES kurse(kurs_id) ON DELETE CASCADE,
    UNIQUE (teilnehmer_id, kurs_id, datum)
);

-- Basic Tests/Evaluations
CREATE TABLE bewertungen (
    bewertung_id SERIAL PRIMARY KEY,
    teilnehmer_id INTEGER NOT NULL,
    kurs_id INTEGER NOT NULL,
    test_typ VARCHAR(20) NOT NULL, -- zwischentest, abschlusstest, muendlich, schriftlich
    test_datum DATE NOT NULL,
    punkte_erreicht DECIMAL(5,2),
    punkte_maximal DECIMAL(5,2),
    note DECIMAL(3,2),
    bestanden BOOLEAN,
    kommentar TEXT,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teilnehmer_id) REFERENCES teilnehmer(teilnehmer_id),
    FOREIGN KEY (kurs_id) REFERENCES kurse(kurs_id)
);

-- =============================================================================
-- HELPER FUNCTIONS AND TRIGGERS
-- =============================================================================

-- Function for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.geaendert_am = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply triggers for automatic timestamp updates
CREATE TRIGGER update_trainer_modified
    BEFORE UPDATE ON trainer
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER update_teilnehmer_modified
    BEFORE UPDATE ON teilnehmer
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER update_kurse_modified
    BEFORE UPDATE ON kurse
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER update_teilnehmer_kurse_modified
    BEFORE UPDATE ON teilnehmer_kurse
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

-- Function to update current student count in courses
CREATE OR REPLACE FUNCTION update_teilnehmer_anzahl()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        UPDATE kurse 
        SET aktuelle_teilnehmer = (
            SELECT COUNT(*) 
            FROM teilnehmer_kurse 
            WHERE kurs_id = OLD.kurs_id 
            AND status IN ('angemeldet', 'aktiv')
        )
        WHERE kurs_id = OLD.kurs_id;
        RETURN OLD;
    ELSE
        UPDATE kurse 
        SET aktuelle_teilnehmer = (
            SELECT COUNT(*) 
            FROM teilnehmer_kurse 
            WHERE kurs_id = NEW.kurs_id 
            AND status IN ('angemeldet', 'aktiv')
        )
        WHERE kurs_id = NEW.kurs_id;
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_teilnehmer_anzahl_trigger
    AFTER INSERT OR UPDATE OR DELETE ON teilnehmer_kurse
    FOR EACH ROW
    EXECUTE FUNCTION update_teilnehmer_anzahl();

-- =============================================================================
-- INDEXES FOR PERFORMANCE
-- =============================================================================

CREATE INDEX idx_trainer_abteilung ON trainer(abteilung_id);
CREATE INDEX idx_kurse_typ ON kurse(kurstyp_id);
CREATE INDEX idx_kurse_raum ON kurse(kursraum_id);
CREATE INDEX idx_kurse_trainer ON kurse(trainer_id);
CREATE INDEX idx_kurse_status ON kurse(status);
CREATE INDEX idx_kurse_datum ON kurse(startdatum, enddatum);
CREATE INDEX idx_teilnehmer_kurse_teilnehmer ON teilnehmer_kurse(teilnehmer_id);
CREATE INDEX idx_teilnehmer_kurse_kurs ON teilnehmer_kurse(kurs_id);
CREATE INDEX idx_anwesenheit_teilnehmer_datum ON anwesenheit(teilnehmer_id, datum);
CREATE INDEX idx_anwesenheit_kurs_datum ON anwesenheit(kurs_id, datum);

-- =============================================================================
-- USEFUL VIEWS
-- =============================================================================

-- Current courses overview
CREATE VIEW v_aktuelle_kurse AS
SELECT 
    k.kurs_id,
    k.kurs_name,
    kt.kurstyp_name,
    a.abteilung_name,
    kr.raum_name,
    t.vorname || ' ' || t.nachname AS trainer_name,
    k.startdatum,
    k.enddatum,
    k.aktuelle_teilnehmer,
    k.max_teilnehmer,
    ROUND(k.aktuelle_teilnehmer::DECIMAL / NULLIF(k.max_teilnehmer, 0) * 100, 1) as auslastung_prozent,
    k.status
FROM kurse k
JOIN kurstypen kt ON k.kurstyp_id = kt.kurstyp_id
JOIN kursraeume kr ON k.kursraum_id = kr.kursraum_id
JOIN abteilungen a ON kr.abteilung_id = a.abteilung_id
JOIN trainer t ON k.trainer_id = t.trainer_id
WHERE k.status IN ('geplant', 'laufend')
ORDER BY k.startdatum;

-- Teacher workload
CREATE VIEW v_trainer_auslastung AS
SELECT 
    t.trainer_id,
    t.vorname || ' ' || t.nachname AS trainer_name,
    t.status,
    COUNT(k.kurs_id) AS anzahl_kurse,
    SUM(k.aktuelle_teilnehmer) AS gesamt_teilnehmer,
    STRING_AGG(k.kurs_name, ', ') AS kurse
FROM trainer t
LEFT JOIN kurse k ON t.trainer_id = k.trainer_id 
    AND k.status IN ('geplant', 'laufend')
WHERE t.aktiv = TRUE
GROUP BY t.trainer_id, t.vorname, t.nachname, t.status
ORDER BY anzahl_kurse DESC;

-- Department utilization
CREATE VIEW v_abteilung_auslastung AS
SELECT 
    a.abteilung_name,
    COUNT(DISTINCT kr.kursraum_id) as verfuegbare_raeume,
    COUNT(DISTINCT k.kurs_id) as aktive_kurse,
    ROUND(COUNT(DISTINCT k.kurs_id)::DECIMAL / NULLIF(COUNT(DISTINCT kr.kursraum_id), 0) * 100, 1) as auslastung_prozent,
    SUM(k.aktuelle_teilnehmer) as gesamt_teilnehmer
FROM abteilungen a
LEFT JOIN kursraeume kr ON a.abteilung_id = kr.abteilung_id AND kr.verfuegbar = TRUE
LEFT JOIN kurse k ON kr.kursraum_id = k.kursraum_id AND k.status IN ('laufend', 'geplant')
WHERE a.aktiv = TRUE
GROUP BY a.abteilung_id, a.abteilung_name;

-- Student attendance summary
CREATE VIEW v_teilnehmer_anwesenheit AS
SELECT 
    t.teilnehmer_id,
    t.vorname || ' ' || t.nachname AS teilnehmer_name,
    k.kurs_name,
    COUNT(a.anwesenheit_id) as tage_gesamt,
    SUM(CASE WHEN a.anwesend THEN 1 ELSE 0 END) as tage_anwesend,
    ROUND(AVG(CASE WHEN a.anwesend THEN 1 ELSE 0 END) * 100, 1) as anwesenheitsquote
FROM teilnehmer t
JOIN teilnehmer_kurse tk ON t.teilnehmer_id = tk.teilnehmer_id
JOIN kurse k ON tk.kurs_id = k.kurs_id
LEFT JOIN anwesenheit a ON t.teilnehmer_id = a.teilnehmer_id AND k.kurs_id = a.kurs_id
WHERE t.aktiv = TRUE AND tk.status IN ('angemeldet', 'aktiv')
GROUP BY t.teilnehmer_id, t.vorname, t.nachname, k.kurs_name
HAVING COUNT(a.anwesenheit_id) > 0;

-- =============================================================================
-- SAMPLE DATA
-- =============================================================================

-- Departments
INSERT INTO abteilungen (abteilung_name, beschreibung) VALUES
('Hauptgebäude', 'Zentrale Verwaltung und Standardkurse'),
('Nebengebäude', 'Spezialkurse und Workshops'),
('Online-Bereich', 'Digitale Kurse und Hybridformate');

-- Classrooms
INSERT INTO kursraeume (abteilung_id, raum_name, kapazitaet, ausstattung) VALUES
(1, 'Raum A101', 12, 'Whiteboard, Beamer, Flipchart'),
(1, 'Raum A102', 15, 'Smartboard, Audioanlage, Tablets'),
(1, 'Raum A103', 10, 'Whiteboard, Gruppenarbeitstische'),
(2, 'Raum B201', 12, 'Whiteboard, Beamer, Sprachlabor'),
(2, 'Raum B202', 8, 'Konversationsecke, Whiteboard'),
(3, 'Online 1', 20, 'Zoom, Moodle, digitale Tafel');

-- Course types
INSERT INTO kurstypen (kurstyp_code, kurstyp_name, beschreibung, level_order) VALUES
('ALPHA', 'Alphabetisierung', 'Lesen und Schreiben lernen', 0),
('A1', 'Deutsch A1', 'Grundstufe - Erste Kenntnisse', 1),
('A2', 'Deutsch A2', 'Grundstufe - Elementare Sprachverwendung', 2),
('B1', 'Deutsch B1', 'Mittelstufe - Selbstständige Sprachverwendung', 3),
('B2', 'Deutsch B2', 'Mittelstufe - Fortgeschritten', 4),
('INTEG', 'Integrationskurs', 'Integrationskurs mit Orientierung', 5),
('BERUF', 'Berufsdeutsch', 'Fachsprache für den Beruf', 6);

-- Sample teachers
INSERT INTO trainer (vorname, nachname, email, telefon, abteilung_id, status, qualifikationen) VALUES
('Maria', 'Schmidt', 'maria.schmidt@institut.de', '030-12345-01', 1, 'verfuegbar', 'DaF/DaZ Zertifikat, 5 Jahre Erfahrung'),
('Hans', 'Müller', 'hans.mueller@institut.de', '030-12345-02', 1, 'im_einsatz', 'Germanistik M.A., DaF Zusatzqualifikation'),
('Anna', 'Weber', 'anna.weber@institut.de', '030-12345-03', 2, 'verfuegbar', 'DaF/DaZ Zertifikat, Alphabetisierung'),
('Thomas', 'Fischer', 'thomas.fischer@institut.de', '030-12345-04', 2, 'im_einsatz', 'Sprachwissenschaft M.A.');

-- =============================================================================
-- UTILITY FUNCTIONS
-- =============================================================================

-- Simple function to enroll a student in a course
CREATE OR REPLACE FUNCTION teilnehmer_anmelden(
    p_teilnehmer_id INTEGER,
    p_kurs_id INTEGER
)
RETURNS BOOLEAN AS $$
DECLARE
    v_max_teilnehmer INTEGER;
    v_aktuelle_teilnehmer INTEGER;
BEGIN
    -- Check course capacity
    SELECT max_teilnehmer, aktuelle_teilnehmer 
    INTO v_max_teilnehmer, v_aktuelle_teilnehmer
    FROM kurse WHERE kurs_id = p_kurs_id;
    
    IF v_aktuelle_teilnehmer >= v_max_teilnehmer THEN
        RETURN FALSE; -- Course is full
    END IF;
    
    -- Enroll student
    INSERT INTO teilnehmer_kurse (teilnehmer_id, kurs_id, status)
    VALUES (p_teilnehmer_id, p_kurs_id, 'angemeldet')
    ON CONFLICT (teilnehmer_id, kurs_id) DO NOTHING;
    
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function to get available courses
CREATE OR REPLACE FUNCTION verfuegbare_kurse()
RETURNS TABLE (
    kurs_id INTEGER,
    kurs_name VARCHAR(200),
    kurstyp_name VARCHAR(100),
    trainer_name TEXT,
    freie_plaetze INTEGER,
    startdatum DATE
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        k.kurs_id,
        k.kurs_name,
        kt.kurstyp_name,
        t.vorname || ' ' || t.nachname,
        k.max_teilnehmer - k.aktuelle_teilnehmer,
        k.startdatum
    FROM kurse k
    JOIN kurstypen kt ON k.kurstyp_id = kt.kurstyp_id
    JOIN trainer t ON k.trainer_id = t.trainer_id
    WHERE k.status = 'geplant' 
    AND k.aktuelle_teilnehmer < k.max_teilnehmer
    ORDER BY k.startdatum;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- COMMENTS AND DOCUMENTATION
-- =============================================================================

COMMENT ON TABLE abteilungen IS 'Departments within the institute';
COMMENT ON TABLE kursraeume IS 'Classrooms available for courses';
COMMENT ON TABLE kurstypen IS 'Course types and levels (A1, A2, B1, etc.)';
COMMENT ON TABLE trainer IS 'Teachers and instructors';
COMMENT ON TABLE teilnehmer IS 'Students enrolled in the institute';
COMMENT ON TABLE kurse IS 'Courses offered';
COMMENT ON TABLE teilnehmer_kurse IS 'Student-course assignments';
COMMENT ON TABLE stundenplan IS 'Weekly schedule for courses';
COMMENT ON TABLE anwesenheit IS 'Daily attendance records';
COMMENT ON TABLE bewertungen IS 'Test results and evaluations';

-- Schema version
CREATE TABLE schema_version (
    version VARCHAR(20) PRIMARY KEY,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

INSERT INTO schema_version (version, description) VALUES 
('1.0.0-simplified', 'Simplified PostgreSQL schema for German course management');

-- =============================================================================
-- END OF SIMPLIFIED SCHEMA
-- =============================================================================