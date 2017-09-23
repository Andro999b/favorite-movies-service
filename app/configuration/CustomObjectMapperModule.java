package configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.google.inject.AbstractModule;
import play.libs.Json;

public class CustomObjectMapperModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CustomObjectMapper.class).asEagerSingleton();
    }

    public static class CustomObjectMapper {
        CustomObjectMapper() {
            ObjectMapper mapper = Json.newDefaultMapper()
                    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                    .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .registerModule(new Hibernate5Module().disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION));

            Json.setObjectMapper(mapper);
        }
    }
}
