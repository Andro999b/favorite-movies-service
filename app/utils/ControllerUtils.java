package utils;

import java.util.Optional;
import java.util.function.Function;

import static play.mvc.Controller.request;

public final class ControllerUtils {
    public static int getIntValueFromQueryString(String parameterName, int defaultValue) {
        return Optional.ofNullable(request().getQueryString(parameterName))
                .map(parameter -> parameterToInt(parameterName, parameter))
                .orElse(defaultValue);
    }

    public static Integer parameterToInt(String parameterName, String parameter) {
        return tryConvertParameterTo(parameterName, Integer::valueOf).apply(parameter);

    }

    public static Long parameterToLong(String parameterName, String parameter) {
        return tryConvertParameterTo(parameterName, Long::valueOf).apply(parameter);
    }

    public static <T> Function<String, T> tryConvertParameterTo(String parameterName, Function<String, T> converter) {
        return parameter -> {
            try {
                return converter.apply(parameter);
            } catch (Exception nfe) {
                throw new IllegalArgumentException("Bad parameter " + parameterName + " value: " + parameter);
            }
        };
    }

    private ControllerUtils(){}
}
