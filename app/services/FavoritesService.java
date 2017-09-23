package services;

import com.google.inject.ImplementedBy;
import dto.ModifyFavoritesListRequest;
import dto.Page;
import dto.Pagination;
import model.FavoritesList;
import model.Movie;
import services.impl.FavoritesServiceImpl;

import java.util.concurrent.CompletionStage;

@ImplementedBy(FavoritesServiceImpl.class)
public interface FavoritesService {
    /**
     * Method return single page with favorites lists
     * @param pagination -  pagination request
     * @return
     */
    Page<FavoritesList> favoritesLists(Pagination pagination);

    /**
     * Method return single favorites list by id
     * @param listId - favorites list id
     * @return
     */
    FavoritesList favoritesList(long listId);

    /**
     * Method create favorites list by create request
     * @param modifyFavoritesListRequest - create favorites list request
     * @return
     */
    FavoritesList createFavoritesList(ModifyFavoritesListRequest modifyFavoritesListRequest);

    /**
     * Method update favorites list by create request
     * @param modifyFavoritesListRequest - update favorites list request
     * @return
     */
    FavoritesList updateFavoritesList(long id, ModifyFavoritesListRequest modifyFavoritesListRequest);

    /**
     * Method delete single favorites list by id
     * @param listId - favorites list id
     * @return
     */
    FavoritesList deleteFavoritesList(long listId);

    /**
     * Method return single page of movies in favorites list
     * @param pagination -  pagination request
     * @param listId - favorites list id
     * @return
     */
    Page<Movie> favoritesListMovies(Pagination pagination, long listId);

    /**
     * Method remove movie from list
     * @param listId - favorite list id
     * @param movieId - movie id
     * @return
     */
    Movie removeMovieFromFavoritesList(long listId, long movieId);

    /**
     * Method add movie to favorite list
     * @param listId  - favorite list id
     * @param movie - movie details
     * @return
     */
    Movie addMovieToFavoritesList(Long listId, Movie movie);
}
