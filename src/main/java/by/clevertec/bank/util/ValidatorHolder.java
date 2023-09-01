package by.clevertec.bank.util;

import org.apache.bval.jsr.ApacheValidationProvider;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * The ValidatorHolder class provides a static method to retrieve a Validator object from a ValidatorFactory.
 */
public final class ValidatorHolder {
    private static final  ValidatorFactory validatorFactory
            = Validation.byProvider(ApacheValidationProvider.class)
            .configure().buildValidatorFactory();

    private ValidatorHolder() {
    }

    /**
     * The function returns a validator object from a validator factory.
     *
     * @return The method is returning an instance of the Validator class.
     */
    public static Validator getValidator() {
        return validatorFactory.getValidator();
    }



}
