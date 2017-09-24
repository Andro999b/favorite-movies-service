package controllers;

import com.typesafe.config.Config;
import dto.ErrorMessage;
import filters.CorsHeadersFilter;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.filters.cors.CORSFilter;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.libs.Json.toJson;
import static play.mvc.Http.Status.CONFLICT;
import static play.mvc.Results.*;

public class ErrorHandler extends DefaultHttpErrorHandler {

    @Inject
    public ErrorHandler(Config config, Environment environment, OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(config, environment, sourceMapper, routes);
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {

        if(exception instanceof CompletionException)//unwrap CompletionException
            exception = exception.getCause();

        return exceptionToResult(exception)
                //filters chain will break after exception, but we still need CORS headers
                .thenApply(result -> CorsHeadersFilter.appendCorsHeaders(result, request));
    }

    private CompletionStage<Result> exceptionToResult(Throwable exception) {
        if(exception instanceof IllegalArgumentException)
            return completedFuture(badRequest(toJson(new ErrorMessage(exception.getMessage()))));

        if(exception instanceof IllegalStateException)
            return completedFuture(status(CONFLICT, toJson(new ErrorMessage(exception.getMessage()))));

        if(exception instanceof NoSuchElementException)
            return completedFuture(notFound(toJson(new ErrorMessage(exception.getMessage()))));

        return completedFuture(internalServerError(toJson(new ErrorMessage(exception.getMessage()))));
    }
}
