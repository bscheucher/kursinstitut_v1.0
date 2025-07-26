package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.dto.TeilnehmerDto;
import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.KursMapper;
import com.bildungsinsitut.deutschkurse.mapper.TeilnehmerMapper;
import com.bildungsinsitut.deutschkurse.model.*;
import com.bildungsinsitut.deutschkurse.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeilnehmerKursServiceTest {

    @Mock
    private TeilnehmerKursRepository teilnehmerKursRepository;

    @Mock
    private TeilnehmerRepository teilnehmerRepository;

    @Mock
    private KursRepository kursRepository;

    @Mock
    private TeilnehmerMapper teilnehmerMapper;

    @Mock
    private KursMapper kursMapper;

    @InjectMocks
    private TeilnehmerKursService teilnehmerKursService;

    private Teilnehmer teilnehmer;
    private Kurs kurs;
    private TeilnehmerKurs teilnehmerKurs;
    private TeilnehmerDto teilnehmerDto;
    private KursDto kursDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        teilnehmer = createTestTeilnehmer();
        kurs = createTestKurs();
        teilnehmerKurs = createTestTeilnehmerKurs();
        teilnehmerDto = createTestTeilnehmerDto();
        kursDto = createTestKursDto();
    }

    @Test
    void shouldEnrollStudentInCourse() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;

        when(teilnehmerRepository.findById(teilnehmerId)).thenReturn(Optional.of(teilnehmer));
        when(kursRepository.findById(kursId)).thenReturn(Optional.of(kurs));
        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.empty());
        when(teilnehmerKursRepository.save(any(TeilnehmerKurs.class))).thenReturn(teilnehmerKurs);
        when(kursRepository.save(any(Kurs.class))).thenReturn(kurs);

        // When
        TeilnehmerKurs result = teilnehmerKursService.enrollTeilnehmerInKurs(teilnehmerId, kursId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TeilnehmerKursStatus.angemeldet);
        assertThat(result.getAnmeldedatum()).isEqualTo(LocalDate.now());

        verify(teilnehmerKursRepository).save(any(TeilnehmerKurs.class));
        verify(kursRepository).save(kursCaptor.capture());

        Kurs savedKurs = kursCaptor.getValue();
        assertThat(savedKurs.getAktuelleTeilnehmer()).isEqualTo(6); // 5 + 1
    }

    @Test
    void shouldThrowExceptionWhenCourseAtCapacity() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;
        kurs.setAktuelleTeilnehmer(12); // At max capacity

        when(teilnehmerRepository.findById(teilnehmerId)).thenReturn(Optional.of(teilnehmer));
        when(kursRepository.findById(kursId)).thenReturn(Optional.of(kurs));
        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teilnehmerKursService.enrollTeilnehmerInKurs(teilnehmerId, kursId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Course is at maximum capacity");

        verify(teilnehmerKursRepository, never()).save(any());
        verify(kursRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStudentAlreadyEnrolled() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;

        when(teilnehmerRepository.findById(teilnehmerId)).thenReturn(Optional.of(teilnehmer));
        when(kursRepository.findById(kursId)).thenReturn(Optional.of(kurs));
        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.of(teilnehmerKurs)); // Already enrolled

        // When & Then
        assertThatThrownBy(() -> teilnehmerKursService.enrollTeilnehmerInKurs(teilnehmerId, kursId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Student is already enrolled in this course");

        verify(teilnehmerKursRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        Integer teilnehmerId = 999;
        Integer kursId = 1;

        when(teilnehmerRepository.findById(teilnehmerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teilnehmerKursService.enrollTeilnehmerInKurs(teilnehmerId, kursId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Teilnehmer not found with id: 999");
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 999;

        when(teilnehmerRepository.findById(teilnehmerId)).thenReturn(Optional.of(teilnehmer));
        when(kursRepository.findById(kursId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teilnehmerKursService.enrollTeilnehmerInKurs(teilnehmerId, kursId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Kurs not found with id: 999");
    }

    @Test
    void shouldRemoveStudentFromCourse() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;

        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.of(teilnehmerKurs));
        when(teilnehmerKursRepository.save(any(TeilnehmerKurs.class))).thenReturn(teilnehmerKurs);
        when(kursRepository.save(any(Kurs.class))).thenReturn(kurs);

        // When
        teilnehmerKursService.removeTeilnehmerFromKurs(teilnehmerId, kursId);

        // Then
        verify(teilnehmerKursRepository).save(teilnehmerKursCaptor.capture());
        verify(kursRepository).save(kursCaptor.capture());

        TeilnehmerKurs savedEnrollment = teilnehmerKursCaptor.getValue();
        assertThat(savedEnrollment.getAbmeldedatum()).isEqualTo(LocalDate.now());
        assertThat(savedEnrollment.getStatus()).isEqualTo(TeilnehmerKursStatus.abgebrochen);

        Kurs savedKurs = kursCaptor.getValue();
        assertThat(savedKurs.getAktuelleTeilnehmer()).isEqualTo(4); // 5 - 1
    }

    @Test
    void shouldUpdateEnrollmentStatus() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;
        TeilnehmerKursStatus newStatus = TeilnehmerKursStatus.aktiv;

        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.of(teilnehmerKurs));
        when(teilnehmerKursRepository.save(any(TeilnehmerKurs.class))).thenReturn(teilnehmerKurs);

        // When
        TeilnehmerKurs result = teilnehmerKursService.updateEnrollmentStatus(teilnehmerId, kursId, newStatus);

        // Then
        assertThat(result).isNotNull();
        verify(teilnehmerKursRepository).save(teilnehmerKursCaptor.capture());

        TeilnehmerKurs savedEnrollment = teilnehmerKursCaptor.getValue();
        assertThat(savedEnrollment.getStatus()).isEqualTo(TeilnehmerKursStatus.aktiv);
    }

    @Test
    void shouldSetAbmeldedatumWhenStatusIsCompleted() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;
        TeilnehmerKursStatus newStatus = TeilnehmerKursStatus.abgeschlossen;

        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.of(teilnehmerKurs));
        when(teilnehmerKursRepository.save(any(TeilnehmerKurs.class))).thenReturn(teilnehmerKurs);

        // When
        teilnehmerKursService.updateEnrollmentStatus(teilnehmerId, kursId, newStatus);

        // Then
        verify(teilnehmerKursRepository).save(teilnehmerKursCaptor.capture());

        TeilnehmerKurs savedEnrollment = teilnehmerKursCaptor.getValue();
        assertThat(savedEnrollment.getStatus()).isEqualTo(TeilnehmerKursStatus.abgeschlossen);
        assertThat(savedEnrollment.getAbmeldedatum()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldGetTeilnehmerInKurs() {
        // Given
        Integer kursId = 1;
        List<TeilnehmerKurs> enrollments = List.of(teilnehmerKurs);

        when(teilnehmerKursRepository.findByKursId(kursId)).thenReturn(enrollments);
        when(teilnehmerMapper.toDto(any(Teilnehmer.class))).thenReturn(teilnehmerDto);

        // When
        List<TeilnehmerDto> result = teilnehmerKursService.getTeilnehmerInKurs(kursId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(teilnehmerDto);
        verify(teilnehmerMapper).toDto(teilnehmer);
    }

    @Test
    void shouldGetKurseForTeilnehmer() {
        // Given
        Integer teilnehmerId = 1;
        List<TeilnehmerKurs> enrollments = List.of(teilnehmerKurs);

        when(teilnehmerKursRepository.findByTeilnehmerId(teilnehmerId)).thenReturn(enrollments);
        when(kursMapper.toDto(any(Kurs.class))).thenReturn(kursDto);

        // When
        List<KursDto> result = teilnehmerKursService.getKurseForTeilnehmer(teilnehmerId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(kursDto);
        verify(kursMapper).toDto(kurs);
    }

    @Test
    void shouldCheckIfStudentIsEnrolled() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;

        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.of(teilnehmerKurs));

        // When
        boolean result = teilnehmerKursService.isStudentEnrolledInCourse(teilnehmerId, kursId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStudentNotEnrolled() {
        // Given
        Integer teilnehmerId = 1;
        Integer kursId = 1;

        when(teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId))
                .thenReturn(Optional.empty());

        // When
        boolean result = teilnehmerKursService.isStudentEnrolledInCourse(teilnehmerId, kursId);

        // Then
        assertThat(result).isFalse();
    }

    // ============ TEST DATA CREATION METHODS ============

    private Teilnehmer createTestTeilnehmer() {
        Teilnehmer teilnehmer = new Teilnehmer();
        teilnehmer.setId(1);
        teilnehmer.setVorname("Max");
        teilnehmer.setNachname("Mustermann");
        teilnehmer.setEmail("max.mustermann@example.com");
        teilnehmer.setAktiv(true);
        return teilnehmer;
    }

    private Kurs createTestKurs() {
        Kurs kurs = new Kurs();
        kurs.setId(1);
        kurs.setKursName("Deutsch A1 - Anfänger");
        kurs.setMaxTeilnehmer(12);
        kurs.setAktuelleTeilnehmer(5);
        kurs.setStatus(KursStatusType.geplant);
        kurs.setStartdatum(LocalDate.now().plusDays(7));
        kurs.setEnddatum(LocalDate.now().plusMonths(3));
        return kurs;
    }

    private TeilnehmerKurs createTestTeilnehmerKurs() {
        TeilnehmerKurs tk = new TeilnehmerKurs();
        tk.setId(1);
        tk.setTeilnehmer(teilnehmer);
        tk.setKurs(kurs);
        tk.setAnmeldedatum(LocalDate.now());
        tk.setStatus(TeilnehmerKursStatus.angemeldet);
        return tk;
    }

    private TeilnehmerDto createTestTeilnehmerDto() {
        TeilnehmerDto dto = new TeilnehmerDto();
        dto.setId(1);
        dto.setVorname("Max");
        dto.setNachname("Mustermann");
        dto.setEmail("max.mustermann@example.com");
        dto.setAktiv(true);
        return dto;
    }

    private KursDto createTestKursDto() {
        KursDto dto = new KursDto();
        dto.setId(1);
        dto.setKursName("Deutsch A1 - Anfänger");
        dto.setMaxTeilnehmer(12);
        dto.setAktuelleTeilnehmer(5);
        dto.setStatus(KursStatusType.geplant);
        return dto;
    }

    // ============ ARGUMENT CAPTORS ============

    @org.mockito.Captor
    private org.mockito.ArgumentCaptor<TeilnehmerKurs> teilnehmerKursCaptor;

    @org.mockito.Captor
    private org.mockito.ArgumentCaptor<Kurs> kursCaptor;
}