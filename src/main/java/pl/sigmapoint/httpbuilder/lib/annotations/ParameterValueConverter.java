package pl.sigmapoint.httpbuilder.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.sigmapoint.httpbuilder.lib.converters.ValueConverter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ParameterValueConverter {
    Class<? extends ValueConverter> value();
}
