package controllers;

import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.typesafe.config.Config;
import dto.ModifyFavoritesListRequest;
import dto.Page;
import dto.Pagination;
import io.swagger.annotations.*;
import model.FavoritesList;
import model.Movie;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.FavoritesService;
import utils.HalLinks;

import javax.inject.Inject;

import static utils.ControllerUtils.getIntValueFromQueryString;
import static utils.ControllerUtils.parameterToLong;
import static utils.HalUtils.toJsonResult;
import static utils.HalUtils.withLinks;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Api
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

    @ApiOperation(value = "Return favorites lists", response = FavoritesList.class, responseContainer = "List")
    @Transactional(readOnly = true)
    public Result favoritesLists() {
        Pagination pagination = pagination();

        Page<FavoritesList> page = favoritesService.favoritesLists(pagination);
        ResourceRepresentation<Page<FavoritesList>> representation =
                ResourceRepresentation.create(page)
                        .withLink(HalLinks.favorites());

        return toJsonResult(OK, withLinks(representation, HalLinks.favoritesPagination(page)));
    }

    @ApiOperation(value = "Return favorite list by id", response = FavoritesList.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "Favorites Lists not found")
    })
    @Transactional(readOnly = true)
    public Result favoritesList(String listId) {
        FavoritesList favoritesList = favoritesService.favoritesList(parameterToLong("listId", listId));
        return toJsonResult(OK, favoritesListToHal(favoritesList));
    }

    @ApiOperation(value = "Return favorite list movies by listId", response = Movie.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Favorites Lists not found")
    })
    @Transactional(readOnly = true)
    public Result favoritesListMovies(String listId) {
        Page<Movie> page = favoritesService.favoritesListMovies(pagination(), parameterToLong("listId", listId));
        ResourceRepresentation<Page<Movie>> representation =
                ResourceRepresentation.create(page)
                        .withLink(HalLinks.favoritesListMovies(listId));

        return toJsonResult(OK, withLinks(representation, HalLinks.favoritesListMoviesPagination(listId, page)));
    }

    @ApiOperation(value = "Create favorites list", response = FavoritesList.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "body",
                    dataType = "dto.ModifyFavoritesListRequest",
                    required = true,
                    paramType = "body"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "Wrong input"),
            @ApiResponse(code = 409, message = "List with this name already exists")
    })
    @Transactional
    public Result createFavoritesList() {
        ModifyFavoritesListRequest modifyFavoritesListRequest = Json.fromJson(
                request().body().asJson(),
                ModifyFavoritesListRequest.class
        );

        FavoritesList favoritesList = favoritesService.createFavoritesList(modifyFavoritesListRequest);
        return toJsonResult(CREATED, favoritesListToHal(favoritesList));
    }

    @ApiOperation("Update favorites list")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "body",
                    dataType = "dto.ModifyFavoritesListRequest",
                    required = true,
                    paramType = "body"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Favorites Lists", response = FavoritesList.class),
            @ApiResponse(code = 400, message = "Wrong input"),
            @ApiResponse(code = 409, message = "List with this name already exists")
    })
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
        return toJsonResult(OK, favoritesListToHal(favoritesList));
    }

    @ApiOperation(value = "Delete favorites list", response = FavoritesList.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "Favorites Lists not found")
    })
    @Transactional
    public Result deleteFavoritesList(String listId) {
        FavoritesList favoritesList = favoritesService.deleteFavoritesList(parameterToLong("listId", listId));
        return toJsonResult(OK, favoritesListToHal(favoritesList));
    }

    @ApiOperation(value = "Add movie to favorites list", response = Movie.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "body",
                    dataType = "model.Movie",
                    required = true,
                    paramType = "body"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 404, message = "Favorites Lists not found")
    })
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

    @ApiOperation(value = "Remove movie from favorites list", response = Movie.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "Favorites Lists not found")
    })
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
