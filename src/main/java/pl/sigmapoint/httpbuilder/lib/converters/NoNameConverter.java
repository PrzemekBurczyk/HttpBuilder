package pl.sigmapoint.httpbuilder.lib.converters;

/**
 * Created by Przemek on 25.02.14.
 */
public class NoNameConverter implements NameConverter {

    @Override
    public String convert(String name) {
        return name;
    }

}
