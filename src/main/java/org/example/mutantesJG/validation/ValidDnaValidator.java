package org.example.mutantesJG.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidDnaValidator implements ConstraintValidator<ValidDna, String[]> {

    private static final Pattern VALID_CHARACTERS = Pattern.compile("^[ATCG]+$");

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null) return false;
        int n = dna.length;
        if (n == 0) return false;

        for (String row : dna) {
            // Verificar matriz cuadrada NxN
            if (row == null || row.length() != n) return false;
            // Verificar caracteres válidos
            if (!VALID_CHARACTERS.matcher(row).matches()) return false;
        }
        return true;
    }
}