package filters;

import akka.stream.Materializer;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

//This filter make application less secure for browsers but more easy to use.
public class CorsHeadersFilter extends Filter {

    @Inject
    public CorsHeadersFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next, Http.RequestHeader rh) {
        if("OPTIONS".equals(rh.method()))
            return CompletableFuture.supplyAsync(() -> appendCorsHeaders(Results.ok(), rh));

        return next.apply(rh).thenApply(result -> appendCorsHeaders(result, rh));
    }

    public static Result appendCorsHeaders(Result result, Http.RequestHeader rh) {
        return result.withHeader(Http.HeaderNames.ALLOW, rh.header(Http.HeaderNames.ORIGIN).orElse("*"))
                .withHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, rh.header(Http.HeaderNames.ORIGIN).orElse("*"))
                .withHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, DELETE, OPTIONS")
                .withHeader(Http.HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent");
    }
}
