package org.example.mutantesJG.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mutantesJG.dto.DnaRequest;
import org.example.mutantesJG.dto.StatsResponse;
import org.example.mutantesJG.service.MutantService;
import org.example.mutantesJG.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Mutant Detector", description = "API para detectar mutantes")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService; // (Implementar lógica similar a MutantService)

    @PostMapping("/mutant")
    @Operation(
            summary = "Detectar si un humano es mutante",
            description = "Detecta si un humano es mutante basándose en su secuencia de ADN. " +
                    "Se busca si existen más de una secuencia de 4 letras iguales de forma oblicua, horizontal o vertical."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Es Mutante (Se detectaron más de una secuencia)"),
            @ApiResponse(responseCode = "403", description = "No es Mutante (Es humano)"),
            @ApiResponse(responseCode = "400", description = "Error de Validación (ADN inválido, matriz no cuadrada, caracteres extraños o null)")
    })
    public ResponseEntity<Void> detectMutant(@Valid @RequestBody DnaRequest request) {
        boolean isMutant = mutantService.analyzeDna(request.getDna());
        if (isMutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de verificaciones")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}