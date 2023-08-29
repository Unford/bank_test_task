package by.clevertec.bank.util;

import org.apache.bval.jsr.ApacheValidationProvider;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public final class ValidatorHolder {
    private static final  ValidatorFactory validatorFactory
            = Validation.byProvider(ApacheValidationProvider.class)
            .configure().buildValidatorFactory();

    private ValidatorHolder() {
    }

    public static Validator getValidator() {
        return validatorFactory.getValidator();
    }



}
