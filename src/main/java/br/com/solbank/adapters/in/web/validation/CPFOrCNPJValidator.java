package br.com.solbank.adapters.in.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CPFOrCNPJValidator implements ConstraintValidator<CPFOrCNPJ, String>{
    private static final Pattern CPF = Pattern.compile("^\\d{11}$");
    private static final Pattern CNPJ = Pattern.compile("^\\d{14}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx){
        if (value == null) return false;
        String digits = value.replaceAll("\\D", "");
        return CPF.matcher(digits).matches() || CNPJ.matcher(digits).matches();
    }

}
