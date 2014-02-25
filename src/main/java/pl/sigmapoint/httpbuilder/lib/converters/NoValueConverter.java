package pl.sigmapoint.httpbuilder.lib.converters;

/**
 * Created by Przemek on 25.02.14.
 */
public class NoValueConverter implements ValueConverter {

    @Override
    public Object convert(Object value) {
        return value;
    }

}
