package org.example.mutantesJG.service;

import org.springframework.stereotype.Service;

@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;

    public boolean isMutant(String[] dna) {
        // 1. Validaciones iniciales para evitar errores
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;

        // Validación de filas nulas, tamaño y caracteres inválidos
        for (String row : dna) {
            if (row == null) return false; // Evita NullPointerException
            if (row.length() != n) return false; // Evita matriz no cuadrada
            if (!row.matches("[ATCG]+")) return false; // Evita caracteres raros
        }

        // 2. Conversión a matriz de caracteres (Optimización)
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        int sequenceCount = 0;

        // 3. Recorrer la matriz buscando secuencias
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                // Búsqueda Horizontal
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkHorizontal(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true; // Early Termination
                    }
                }

                // Búsqueda Vertical
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkVertical(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                // Búsqueda Diagonal Descendente (↘)
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalDescending(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                // Búsqueda Diagonal Ascendente (↗)
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalAscending(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkHorizontal(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row][col+1] == base &&
                matrix[row][col+2] == base &&
                matrix[row][col+3] == base;
    }

    private boolean checkVertical(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row+1][col] == base &&
                matrix[row+2][col] == base &&
                matrix[row+3][col] == base;
    }

    private boolean checkDiagonalDescending(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row+1][col+1] == base &&
                matrix[row+2][col+2] == base &&
                matrix[row+3][col+3] == base;
    }

    private boolean checkDiagonalAscending(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row-1][col+1] == base &&
                matrix[row-2][col+2] == base &&
                matrix[row-3][col+3] == base;
    }
}