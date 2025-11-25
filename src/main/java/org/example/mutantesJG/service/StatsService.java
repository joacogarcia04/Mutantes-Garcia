package org.example.mutantesJG.service;

import lombok.RequiredArgsConstructor;
import org.example.mutantesJG.dto.StatsResponse;
import org.example.mutantesJG.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // <--- Esto busca variables 'final'
public class StatsService {

    // ¡IMPORTANTE! Debe decir 'final' para que @RequiredArgsConstructor funcione
    private final DnaRecordRepository repository;

    public StatsResponse getStats() {
        long countMutant = repository.countByIsMutant(true);
        long countHuman = repository.countByIsMutant(false);

        double ratio;

        if (countHuman == 0) {
            ratio = countMutant > 0 ? countMutant : 0.0;
        } else {
            ratio = (double) countMutant / countHuman;
        }

        return new StatsResponse(countMutant, countHuman, ratio);
    }
}