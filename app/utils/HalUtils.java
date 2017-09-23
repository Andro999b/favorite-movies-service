package utils;

import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import play.libs.Json;

import java.util.Collection;

public final class HalUtils {
    private static final JsonRepresentationWriter jsonRepresentationWriter = JsonRepresentationWriter.create(Json.mapper());

    public static String toJson(ResourceRepresentation<?> representation) {
        return jsonRepresentationWriter.print(representation).utf8();
    }

    public static <T> ResourceRepresentation<T> withLinks(ResourceRepresentation<T> representation, Collection<Link> links) {
        for (Link link : links) {
            representation = representation.withLink(link);
        }
        return representation;
    }

    private HalUtils() {}
}
