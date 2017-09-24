package utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import play.http.HttpEntity;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Collection;

public final class HalUtils {
    private static final JsonRepresentationWriter jsonRepresentationWriter = JsonRepresentationWriter.create(Json.mapper());

    public static Result toJsonResult(int status, ResourceRepresentation<?> representation) {
        return new Result(
                status,
                HttpEntity.fromString(jsonRepresentationWriter.print(representation).utf8(), JsonEncoding.UTF8.getJavaName())
        ).as("application/json; charset=" + JsonEncoding.UTF8.getJavaName());
    }

    public static <T> ResourceRepresentation<T> withLinks(ResourceRepresentation<T> representation, Collection<Link> links) {
        for (Link link : links) {
            representation = representation.withLink(link);
        }
        return representation;
    }

    private HalUtils() {
    }
}
