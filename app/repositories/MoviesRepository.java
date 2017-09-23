package repositories;

import com.google.inject.ImplementedBy;
import dto.Page;
import dto.Pagination;
import model.Movie;
import repositories.impl.MoviesRepositoryImpl;

import java.util.Optional;

@ImplementedBy(MoviesRepositoryImpl.class)
public interface MoviesRepository {
    Optional<Movie> findById(Long movieId);
    Page<Movie> findByFavoriteListId(Pagination pagination, Long listId);
    Movie save(Movie movie);
}
