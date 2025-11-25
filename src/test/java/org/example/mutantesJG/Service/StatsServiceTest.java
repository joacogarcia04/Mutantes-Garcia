package org.example.mutantesJG.Service; // Asegúrate que el paquete sea correcto

import org.example.mutantesJG.dto.StatsResponse;
import org.example.mutantesJG.repository.DnaRecordRepository;
import org.example.mutantesJG.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository repository; // El nombre debe coincidir (o ser del mismo tipo)

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Debe calcular estadísticas correctamente (Ratio 0.4)")
    void testGetStatsWithData() {
        // ARRANGE
        when(repository.countByIsMutant(true)).thenReturn(40L);
        when(repository.countByIsMutant(false)).thenReturn(100L);

        // ACT
        StatsResponse stats = statsService.getStats();

        // ASSERT
        // Asegúrate de usar los getters que tienes en tu DTO (StatsResponse)
        // Si usas snake_case en DTO, usa getCount_mutant_dna()
        // Si usas camelCase, usa getCountMutantDna()
        assertEquals(40, stats.getCountMutantDna());
        assertEquals(100, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe manejar división por cero (Sin humanos)")
    void testGetStatsWithNoHumans() {
        when(repository.countByIsMutant(true)).thenReturn(10L);
        when(repository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(10, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(10.0, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe retornar ceros si no hay datos")
    void testGetStatsWithNoData() {
        when(repository.countByIsMutant(true)).thenReturn(0L);
        when(repository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(0, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001);
    }
}