package org.example.mutantesJG.Service;

import org.example.mutantesJG.entity.DnaRecord; // Asegúrate que tu entidad se llame así
import org.example.mutantesJG.repository.DnaRecordRepository;
import org.example.mutantesJG.service.MutantDetector;
import org.example.mutantesJG.service.MutantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    @Test
    @DisplayName("Debe analizar ADN mutante y guardarlo en DB")
    void testAnalyzeMutantDnaAndSave() {
        // ARRANGE
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        // Simulamos que NO existe en BD
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        // Simulamos que el detector dice que ES mutante
        when(mutantDetector.isMutant(dna)).thenReturn(true);
        // Simulamos el guardado
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // ACT
        boolean result = mutantService.analyzeDna(dna);

        // ASSERT
        assertTrue(result);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe analizar ADN humano y guardarlo en DB")
    void testAnalyzeHumanDnaAndSave() {
        // ARRANGE
        String[] dna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(dna)).thenReturn(false); // ES humano
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // ACT
        boolean result = mutantService.analyzeDna(dna);

        // ASSERT
        assertFalse(result);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado si el ADN ya fue analizado")
    void testReturnCachedResultForAnalyzedDna() {
        // ARRANGE
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        DnaRecord existingRecord = new DnaRecord();
        existingRecord.setMutant(true); // Ya sabíamos que era mutante

        // Simulamos que SÍ existe en BD
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(existingRecord));

        // ACT
        boolean result = mutantService.analyzeDna(dna);

        // ASSERT
        assertTrue(result);
        // VERIFY: Importante validar que NO se llamó al detector de nuevo (ahorro de recursos)
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }
}