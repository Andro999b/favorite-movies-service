package controllers;

import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.typesafe.config.Config;
import dto.ModifyFavoritesListRequest;
import dto.Page;
import dto.Pagination;
import model.FavoritesList;
import model.Movie;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.FavoritesService;
import utils.HalLinks;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static utils.ControllerUtils.getIntValueFromQueryString;
import static utils.ControllerUtils.parameterToLong;
import static utils.HalUtils.toJson;
import static utils.HalUtils.withLinks;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class FavoritesController extends Controller {
    private final FavoritesService favoritesService;
    private final int defaultPageSize;

    @Inject
    public FavoritesController(
            FavoritesService favoritesService,
            Config config) {
        this.favoritesService = favoritesService;
        defaultPageSize = config.getInt("pagiantion.pageSize");
    }

    @Transactional(readOnly = true)
    public Result favoritesLists() {
        Pagination pagination = pagination();

        Page<FavoritesList> page = favoritesService.favoritesLists(pagination);
        ResourceRepresentation<Page<FavoritesList>> representation =
                ResourceRepresentation.create(page)
                        .withLink(HalLinks.favorites());

        return ok(toJson(withLinks(representation, HalLinks.favoritesPagination(page))));
    }

    @Transactional(readOnly = true)
    public Result favoritesList(String listId) {
        FavoritesList favoritesList = favoritesService.favoritesList(parameterToLong("listId", listId));
        return ok(toJson(favoritesListToHal(favoritesList)));
    }

    @Transactional(readOnly = true)
    public Result favoritesListMovies(String listId) {
        Page<Movie> page = favoritesService.favoritesListMovies(pagination(), parameterToLong("listId", listId));
        ResourceRepresentation<Page<Movie>> representation =
                ResourceRepresentation.create(page)
                        .withLink(HalLinks.favoritesListMovies(listId));

        return ok(toJson(withLinks(representation, HalLinks.favoritesListMoviesPagination(listId, page))));
    }

    @Transactional
    public Result createFavoritesList() {
        ModifyFavoritesListRequest modifyFavoritesListRequest = Json.fromJson(
                request().body().asJson(),
                ModifyFavoritesListRequest.class
        );

        FavoritesList favoritesList = favoritesService.createFavoritesList(modifyFavoritesListRequest);
        return created(toJson(favoritesListToHal(favoritesList)));
    }

    @Transactional
    public Result updateFavoritesList(String listId) {
        ModifyFavoritesListRequest modifyFavoritesListRequest = Json.fromJson(
                request().body().asJson(),
                ModifyFavoritesListRequest.class
        );


        FavoritesList favoritesList = favoritesService.updateFavoritesList(
                parameterToLong("listId", listId),
                modifyFavoritesListRequest
        );
        return ok(toJson(favoritesListToHal(favoritesList)));
    }

    @Transactional
    public Result deleteFavoritesList(String listId) {
        FavoritesList favoritesList = favoritesService.deleteFavoritesList(parameterToLong("listId", listId));
        return ok(toJson(favoritesListToHal(favoritesList)));
    }

    @Transactional
    public Result addMovieToFavoritesList(String listId) {
        Movie movie = Json.fromJson(
                request().body().asJson(),
                Movie.class
        );

        return created(Json.toJson(favoritesService.addMovieToFavoritesList(
                parameterToLong("listId", listId),
                movie
        )));
    }

    @Transactional
    public Result removeMovieFromFavoritesList(String listId, String movieId) {
        return ok(Json.toJson(favoritesService.removeMovieFromFavoritesList(
                parameterToLong("listId", listId),
                parameterToLong("movieId", movieId)
        )));
    }

    private Pagination pagination() {
        int page = getIntValueFromQueryString("page", 1);
        int pageSize = getIntValueFromQueryString("page_size", defaultPageSize);

        return new Pagination(page, pageSize);
    }

    private static ResourceRepresentation<FavoritesList> favoritesListToHal(FavoritesList favoritesList) {
        return ResourceRepresentation.create(favoritesList)
                .withLink(HalLinks.favoritesList(favoritesList.getId()))
                .withLink(HalLinks.favoritesListMovies(favoritesList.getId()));
    }
}
