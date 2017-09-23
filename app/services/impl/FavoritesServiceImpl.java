package services.impl;

import dto.ModifyFavoritesListRequest;
import model.FavoritesList;
import model.Movie;
import dto.Page;
import dto.Pagination;
import org.apache.commons.lang3.StringUtils;
import play.db.jpa.Transactional;
import repositories.FavoritesRepository;
import repositories.MoviesRepository;
import services.FavoritesService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.Collections.singleton;

@Singleton
public class FavoritesServiceImpl implements FavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final MoviesRepository moviesRepository;

    @Inject
    public FavoritesServiceImpl(FavoritesRepository favoritesRepository, MoviesRepository moviesRepository) {
        this.favoritesRepository = favoritesRepository;
        this.moviesRepository = moviesRepository;
    }

    @Override
    public Page<FavoritesList> favoritesLists(Pagination pagination) {
        return favoritesRepository.find(pagination);
    }

    @Override
    public FavoritesList favoritesList(long listId) {
        return favoritesRepository.findById(listId)
                    .orElseThrow(
                            () -> new NoSuchElementException("Favorites list with id=" + listId + " not found")
                    );
    }

    @Override
    public FavoritesList createFavoritesList(ModifyFavoritesListRequest modifyFavoritesListRequest) {
        validateName(modifyFavoritesListRequest);

        String name = modifyFavoritesListRequest.getName();

        favoritesRepository.findByName(name)
                .ifPresent(existsList -> {
                    throw new IllegalStateException("Favorites list with name " + name + " already exits");
                });

        FavoritesList favoritesList = new FavoritesList();
        favoritesList.setName(name);

        return favoritesRepository.save(favoritesList);
    }

    @Override
    public FavoritesList updateFavoritesList(long id, ModifyFavoritesListRequest modifyFavoritesListRequest) {
        validateName(modifyFavoritesListRequest);

        String name = modifyFavoritesListRequest.getName();

        favoritesRepository.findByName(name)
                .ifPresent(existsList -> {
                    if(!Objects.equals(existsList.getId(), id))
                        throw new IllegalStateException("Favorites list with name " + name + " already exits");
                });

        FavoritesList favoritesList = new FavoritesList();
        favoritesList.setId(id);
        favoritesList.setName(name);

        return favoritesRepository.save(favoritesList);
    }

    @Override
    public FavoritesList deleteFavoritesList(long id) {
        return favoritesRepository.delete(favoritesList(id));
    }

    @Override
    public Page<Movie> favoritesListMovies(Pagination pagination, long listId) {
        return moviesRepository.findByFavoriteListId(pagination, listId);
    }

    @Override
    public Movie removeMovieFromFavoritesList(long listId, long movieId) {
        FavoritesList favoritesList = favoritesList(listId);
        Movie movie = moviesRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("Movie with id=" + movieId + " not found"));

        movie.getFavoritesLists().remove(favoritesList);

        return moviesRepository.save(movie);
    }

    @Override
    public Movie addMovieToFavoritesList(Long listId, Movie movie) {
        FavoritesList favoritesList = favoritesList(listId);

        Optional<Movie> movieOptional = moviesRepository.findById(movie.getId());
        if(movieOptional.isPresent()) {
            movie = movieOptional.get();
            movie.getFavoritesLists().add(favoritesList);
        } else {
            movie.setFavoritesLists(singleton(favoritesList));
        }

        return moviesRepository.save(movie);
    }

    private void validateName(ModifyFavoritesListRequest modifyFavoritesListRequest) {
        if(StringUtils.isEmpty(modifyFavoritesListRequest.getName()))
            throw new IllegalArgumentException("Favorites list name can not be empty");
    }
}
