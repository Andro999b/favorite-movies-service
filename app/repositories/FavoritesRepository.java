package repositories;

import com.google.inject.ImplementedBy;
import dto.Page;
import dto.Pagination;
import model.FavoritesList;
import repositories.impl.FavoritesRepositoryImpl;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ImplementedBy(FavoritesRepositoryImpl.class)
public interface FavoritesRepository {
    Page<FavoritesList> find(Pagination pagination);
    Optional<FavoritesList> findById(long id);
    Optional<FavoritesList> findByName(String name);

    FavoritesList save(FavoritesList favoritesList);
    FavoritesList delete(FavoritesList favoritesList);
}
