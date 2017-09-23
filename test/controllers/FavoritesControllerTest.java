package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import dto.ModifyFavoritesListRequest;
import model.FavoritesList;
import model.Movie;
import org.apache.commons.lang3.RandomUtils;
import org.eclipse.jetty.io.RuntimeIOException;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static play.mvc.Http.HttpVerbs.DELETE;
import static play.mvc.Http.HttpVerbs.GET;
import static play.mvc.Http.HttpVerbs.POST;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.PUT;
import static play.test.Helpers.*;

public class FavoritesControllerTest extends WithApplication {
    private static List<Movie> moviesTestData;

    @BeforeClass
    public static void readMoviesTestData() {
        InputStream inputStream = FavoritesControllerTest.class.getClassLoader().getResourceAsStream("movies.json");

        TypeReference<List<Movie>> typeReference = new TypeReference<List<Movie>>() {
        };

        ObjectMapper mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            moviesTestData = unmodifiableList(mapper.readValue(inputStream, typeReference));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void shouldCreateFavoriteList() {
        final String name = "shouldCreateFavoriteList";

        FavoritesList favoritesList = createFavoritesList(name);
        assertEquals(name, favoritesList.getName());
        assertNotNull(favoritesList.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateTwoFavoriteListWithSameName() {
        final String name = "shouldNotAbleToCreateTwoFavoriteListWithSameName";

        createFavoritesList(name);
        createFavoritesList(name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateWithEmptyName() {
        final String name = "";

        createFavoritesList(name);
    }

    @Test
    public void shouldUpdateFavoritesListName() {
        String name = "shouldUpdateFavoritesListName";

        Long id = createFavoritesList(name).getId();

        name += "_updated";
        FavoritesList favoritesList = updateFavoritesList(id, name);

        assertEquals(id, favoritesList.getId());
        assertEquals(name, favoritesList.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAbleToSetFavoritesListName() {
        String name = "shouldNotAbleToSetFavoritesListName";

        Long id = createFavoritesList(name).getId();

        name = "";
        updateFavoritesList(id, name);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAbleToSetExistsFavoritesListName() {
        String name1 = "shouldNotAbleToSetExistsFavoritesListName1";
        String name2 = "shouldNotAbleToSetExistsFavoritesListName2";

        Long id = createFavoritesList(name1).getId();
        createFavoritesList(name2);

        updateFavoritesList(id, name2);
    }

    @Test
    public void shouldReturnFavoritesListById() {
        final String name = "shouldAbleToGetFavoritesListById";

        Long id = createFavoritesList(name).getId();

        FavoritesList favoritesList = getFavoritesList(id);

        assertEquals(name, favoritesList.getName());
        assertEquals(id, favoritesList.getId());
    }

    @Test
    public void shouldReturnFavoritesListPageByPage() {
        Function<Integer, String> createName = i -> "shouldReturnReturnFavoritesListPageByPage" + i;
        testPageByPage(21, "/favorites", i -> createFavoritesList(createName.apply(i)));
    }

    @Test
    public void shouldDeleteFavoritesList() {
        final String name = "shouldAbleToDeleteFavoritesList";

        Long id = createFavoritesList(name).getId();
        Result result = deleteFavoritesList(id);

        assertEquals(OK, result.status());

        try {
            getFavoritesList(id);
        } catch (NoSuchElementException e) {
            assertThat(e.getMessage(), containsString("not found"));
        }
    }

    @Test
    public void shouldAbleToCreateFavoriteListWithSameNameAfterDelete() {
        final String name = "shouldAbleToCreateFavoriteListWithSameNameAfterDelete";

        Long id = createFavoritesList(name).getId();
        deleteFavoritesList(id);

        id = createFavoritesList(name).getId();

        FavoritesList favoritesList = getFavoritesList(id);

        assertEquals(name, favoritesList.getName());
        assertEquals(id, favoritesList.getId());
    }

    @Test
    public void shouldAddMovieToList() {
        Movie movie = getRandomTestMovie();
        Long listId = createFavoritesList("shouldAddMovieToList").getId();

        Movie returnedMovie = addMovieToList(movie, listId);

        assertMovie(movie, returnedMovie);

        //validate movies list
        List<Movie> movieList = getMoviesInList(listId);

        assertEquals(1, movieList.size());
        assertMovie(movie, movieList.get(0));
    }

    @Test
    public void shouldAddMovieToMoreThatOneList() {
        Movie movie = getRandomTestMovie();

        Long listId1 = createFavoritesList("shouldAddMovieToList1").getId();
        Long listId2 = createFavoritesList("shouldAddMovieToList2").getId();

        Movie returnedMovie1 = addMovieToList(movie, listId1);
        Movie returnedMovie2 = addMovieToList(movie, listId2);

        assertMovie(movie, returnedMovie1);
        assertMovie(movie, returnedMovie2);
    }

    @Test
    public void shouldRemoveMovieToList() {
        Movie movie = getRandomTestMovie();
        Long listId = createFavoritesList("shouldRemoveMovieToList").getId();

        movie = addMovieToList(movie, listId);
        removeMovieToList(movie.getId(), listId);

        assertTrue(getMoviesInList(listId).isEmpty());
    }

    @Test
    public void shouldAbleToGetMovieThatRemovedFromOtherList() {
        Movie movie = getRandomTestMovie();

        Long listId1 = createFavoritesList("shouldAbleToGetMovieThatRemovedFromOtherList1").getId();
        Long listId2 = createFavoritesList("shouldAbleToGetMovieThatRemovedFromOtherList2").getId();

        addMovieToList(movie, listId1);
        addMovieToList(movie, listId2);

        removeMovieToList(movie.getId(), listId1);

        assertTrue(getMoviesInList(listId1).isEmpty());
        assertFalse(getMoviesInList(listId2).isEmpty());
    }

    @Test
    public void shouldReturnMoviesPageByPage() {
        Long listId = createFavoritesList("shouldReturnMoviesPageByPage").getId();

        testPageByPage(
                moviesTestData.size(),
                "/favorites/" + listId + "/movies",
                i -> addMovieToList(moviesTestData.get(i), listId))
        ;
    }

    private FavoritesList getFavoritesList(Long id) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/favorites/" + id);

        return formJson(route(app, request), FavoritesList.class);
    }

    private Result deleteFavoritesList(Long id) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/favorites/" + id);

        return route(app, request);
    }

    private FavoritesList createFavoritesList(String name) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(new ModifyFavoritesListRequest(name)))
                .uri("/favorites");

        Result result = route(app, request);
        assertEquals(CREATED, result.status());

        return formJson(result, FavoritesList.class);
    }

    private Movie addMovieToList(Movie movie, Long listId) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .bodyJson(Json.toJson(movie))
                .method(POST)
                .uri("/favorites/" + listId + "/movies");

        Result result = route(app, request);
        assertEquals(CREATED, result.status());
        return formJson(result, Movie.class);
    }

    private void removeMovieToList(Long movieId, Long listId) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/favorites/" + listId + "/movies/" + movieId);

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    private List<Movie> getMoviesInList(Long listId) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/favorites/" + listId + "/movies");

        Result result = route(app, request);
        assertEquals(OK, result.status());

        try {
            JsonNode jsonPage = Json.mapper().readTree(contentAsString(result));
            ObjectReader objectReader = Json.mapper().readerFor(new TypeReference<List<Movie>>() {
            });
            return objectReader.readValue(jsonPage.findPath("results"));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private FavoritesList updateFavoritesList(Long id, String name) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(Json.toJson(new ModifyFavoritesListRequest(name)))
                .uri("/favorites/" + id);

        Result result = route(app, request);
        assertEquals(OK, result.status());

        return formJson(result, FavoritesList.class);
    }

    private static <T> T formJson(Result result, Class<T> clazz) {
        return Json.fromJson(Json.parse(contentAsString(result)), clazz);
    }

    private Movie getRandomTestMovie() {
        int index = RandomUtils.nextInt() % moviesTestData.size();
        return moviesTestData.get(index);
    }

    private void assertMovie(Movie expected, Movie actual) {
        assertEquals(expected, actual);
        assertEquals(expected.getOverview(), actual.getOverview());
        assertEquals(expected.getPopularity(), actual.getPopularity());
        assertEquals(expected.getVoteCount(), actual.getVoteCount());
        assertEquals(expected.getVoteAverage(), actual.getVoteAverage());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getReleaseDate(), actual.getReleaseDate());
    }

    private void testPageByPage(int totalItems, String basePath, Consumer<Integer> createItem) {
        final int pageSize = 4;
        final int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        Stream.iterate(0, i -> i + 1)
                .limit(totalItems)
                .forEach(createItem);

        for (int i = 0; i < totalPages; i++) {
            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri(basePath + "?page=" + i + "&page_size=" + pageSize);


            Map response = formJson(route(app, request), Map.class);

            assertThat(response.get("page"), equalTo(i));
            assertThat(response.get("page_size"), equalTo(pageSize));
            assertThat(response.get("total_pages"), equalTo(totalPages));
            assertThat(response.get("total_results"), equalTo(totalItems));
            assertThat((Collection<?>) response.get("results"), anyOf(hasSize(pageSize), hasSize(totalItems % pageSize)));
        }
    }
}
