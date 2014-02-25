package pl.sigmapoint.httpbuilder.lib.builder;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pl.sigmapoint.httpbuilder.lib.annotations.NotParameter;
import pl.sigmapoint.httpbuilder.lib.annotations.ParameterName;
import pl.sigmapoint.httpbuilder.lib.annotations.ParameterNameConverter;
import pl.sigmapoint.httpbuilder.lib.annotations.ParameterType;
import pl.sigmapoint.httpbuilder.lib.annotations.ParameterValueConverter;
import pl.sigmapoint.httpbuilder.lib.converters.NameConverter;
import pl.sigmapoint.httpbuilder.lib.converters.ValueConverter;
import pl.sigmapoint.httpbuilder.lib.enums.HttpParameterType;

public class HttpParameterBuilder {

    private static final HttpParameterType defaultParameterType = HttpParameterType.BODY;

    /**
     * Global rules
     */
    private NameConverter globalNameConverter;
    private ValueConverter globalValueConverter;
    private HttpParameterType globalHttpParameterType;

    /**
     * Local (per field) rules
     */
    private NameConverter localNameConverter;
    private ValueConverter localValueConverter;
    private HttpParameterType localHttpParameterType;

    private String encoding = "UTF-8";

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String buildQueryParams(Object object) throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        List<NameValuePair> queryParams = buildParams(object, HttpParameterType.QUERY);
        return URLEncodedUtils.format(queryParams, encoding);
    }

    public HttpEntity buildHttpEntity(Object object) throws IllegalAccessException, NoSuchMethodException, InstantiationException, UnsupportedEncodingException {
        List<NameValuePair> formParams = buildParams(object, HttpParameterType.BODY);
        return new UrlEncodedFormEntity(formParams, encoding);
    }

    public List<NameValuePair> buildParams(Object object, HttpParameterType parameterType) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        resetRules();
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        loadGlobalRules(clazz);

        for (Field field : fields) {

            if (isParameter(field)) {

                field.setAccessible(true);
                resetLocalRules();
                loadLocalRules(field);

                if (isDesiredParameterType(parameterType)) {

                    String key = getParameterName(field);
                    Object value = getParameterValue(field, object);

                    key = convertKey(key);
                    value = convertValue(value);

                    params.add(new BasicNameValuePair(key, value.toString()));
                }
            }
        }

        return params;
    }

    private boolean isDesiredParameterType(HttpParameterType parameterType) {
        if (localHttpParameterType != null) {
            return parameterType.equals(localHttpParameterType);
        } else if (globalHttpParameterType != null) {
            return parameterType.equals(globalHttpParameterType);
        } else {
            return parameterType.equals(defaultParameterType);
        }
    }

    private boolean isParameter(Field field) {
        return field.getAnnotation(NotParameter.class) == null;
    }


    private Object convertValue(Object value) {
        if (localValueConverter != null) {
            return localValueConverter.convert(value);
        } else if (globalValueConverter != null) {
            return globalValueConverter.convert(value);
        } else {
            return value;
        }
    }

    private String convertKey(String key) {
        if (localNameConverter != null) {
            return localNameConverter.convert(key);
        } else if (globalNameConverter != null) {
            return globalNameConverter.convert(key);
        } else {
            return key;
        }
    }

    private void resetRules() {
        resetGlobalRules();
        resetLocalRules();
    }

    private void resetGlobalRules() {
        globalNameConverter = null;
        globalValueConverter = null;
        globalHttpParameterType = null;
    }

    private void resetLocalRules() {
        localNameConverter = null;
        localValueConverter = null;
        localHttpParameterType = null;
    }

    private void loadGlobalRules(Class clazz) throws IllegalAccessException, InstantiationException {
        ParameterNameConverter parameterNameConverter = ((ParameterNameConverter) clazz.getAnnotation(ParameterNameConverter.class));
        if (parameterNameConverter != null) {
            globalNameConverter = parameterNameConverter.value().newInstance();
        }
        ParameterValueConverter parameterValueConverter = (ParameterValueConverter) clazz.getAnnotation(ParameterValueConverter.class);
        if (parameterValueConverter != null) {
            globalValueConverter = parameterValueConverter.value().newInstance();
        }
        ParameterType parameterType = (ParameterType) clazz.getAnnotation(ParameterType.class);
        if (parameterType != null) {
            globalHttpParameterType = parameterType.value();
        }
    }

    private void loadLocalRules(Field field) throws IllegalAccessException, InstantiationException {
        ParameterNameConverter parameterNameConverter = field.getAnnotation(ParameterNameConverter.class);
        if (parameterNameConverter != null) {
            localNameConverter = parameterNameConverter.value().newInstance();
        }
        ParameterValueConverter parameterValueConverter = field.getAnnotation(ParameterValueConverter.class);
        if (parameterValueConverter != null) {
            localValueConverter = parameterValueConverter.value().newInstance();
        }
        ParameterType parameterType = field.getAnnotation(ParameterType.class);
        if (parameterType != null) {
            localHttpParameterType = parameterType.value();
        }
    }

    private String getParameterName(Field field) {
        ParameterName parameterName = field.getAnnotation(ParameterName.class);
        if (parameterName != null) {
            return parameterName.value();
        } else {
            return field.getName();
        }
    }

    private Object getParameterValue(Field field, Object object) throws IllegalAccessException {
        return field.get(object);
    }

}
