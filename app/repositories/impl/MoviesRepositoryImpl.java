package repositories.impl;

import dto.Page;
import dto.Pagination;
import model.FavoritesList;
import model.Movie;
import play.db.jpa.JPAApi;
import repositories.MoviesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Singleton
public class MoviesRepositoryImpl implements MoviesRepository {
    private final JPAApi jpaApi;

    @Inject
    public MoviesRepositoryImpl(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Override
    public Optional<Movie> findById(Long movieId) {
        return Optional.ofNullable(jpaApi.em().find(Movie.class, movieId));
    }

    @Override
    public Page<Movie> findByFavoriteListId(Pagination pagination, Long listId) {
        EntityManager em = jpaApi.em();
        int total = ((Number) em.createQuery(
                "select count(*) from Movie m " +
                        "left join m.favoritesLists f " +
                        "where f.id = :listId"
        )
                .setParameter("listId", listId)
                .getSingleResult()).intValue();

        List<Movie> results = em.createQuery(
                "select m from Movie m " +
                        "left join m.favoritesLists f " +
                        "where f.id = :listId",
                Movie.class)
                .setParameter("listId", listId)
                .setFirstResult(pagination.getOffset())
                .setMaxResults(pagination.getPageSize())
                .getResultList();

        return new Page<>(pagination, total, results);
    }

    @Override
    public Movie save(Movie movie) {
        return jpaApi.em().merge(movie);
    }
}
