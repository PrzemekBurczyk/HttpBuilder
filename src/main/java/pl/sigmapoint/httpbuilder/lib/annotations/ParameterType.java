package pl.sigmapoint.httpbuilder.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.sigmapoint.httpbuilder.lib.enums.HttpParameterType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ParameterType {
    HttpParameterType value();
}
