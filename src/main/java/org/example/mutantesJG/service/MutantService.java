package org.example.mutantesJG.service;

import lombok.RequiredArgsConstructor;
import org.example.mutantesJG.entity.DnaRecord;
import org.example.mutantesJG.exception.InvalidDnaException;
import org.example.mutantesJG.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository repository;

    public boolean analyzeDna(String[] dna) {

        if (dna == null || dna.length == 0) {
            throw new InvalidDnaException("El ADN no puede ser nulo ni vacío");
        }

        String hash = calculateHash(dna);

        Optional<DnaRecord> existingRecord = repository.findByDnaHash(hash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant();
        }

        boolean isMutant = mutantDetector.isMutant(dna);

        DnaRecord record = DnaRecord.builder()
                .dnaHash(hash)
                .isMutant(isMutant)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(record);

        return isMutant;
    }

    private String calculateHash(String[] dna) {
        try {
            String rawDna = String.join("", dna);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(rawDna.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular hash SHA-256", e);
        }
    }
}