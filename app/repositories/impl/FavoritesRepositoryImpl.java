package repositories.impl;

import dto.Page;
import dto.Pagination;
import model.FavoritesList;
import play.db.jpa.JPAApi;
import repositories.FavoritesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static utils.JpaUtils.uniqueResult;

@Singleton
public class FavoritesRepositoryImpl implements FavoritesRepository {
    private final JPAApi jpaApi;

    @Inject
    public FavoritesRepositoryImpl(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Override
    public Page<FavoritesList> find(Pagination pagination) {
        EntityManager em = jpaApi.em();
        int total = ((Number) em.createQuery("select count(*) from FavoritesList")
                .getSingleResult()).intValue();

        List<FavoritesList> results = em.createQuery("from FavoritesList", FavoritesList.class)
                .setFirstResult(pagination.getOffset())
                .setMaxResults(pagination.getPageSize())
                .getResultList();

        return new Page<>(pagination, total, results);

    }

    @Override
    public Optional<FavoritesList> findById(long id) {
        EntityManager em = jpaApi.em();
        return Optional.ofNullable(em.find(FavoritesList.class, id));
    }

    @Override
    public Optional<FavoritesList> findByName(String name) {
        EntityManager em = jpaApi.em();
        return uniqueResult(
                em.createQuery("from FavoritesList where name=:name", FavoritesList.class)
                        .setParameter("name", name)
        );
    }

    @Override
    public FavoritesList save(FavoritesList favoritesList) {
        EntityManager em = jpaApi.em();
        return  em.merge(favoritesList);
    }

    @Override
    public FavoritesList delete(FavoritesList favoritesList) {
        EntityManager em = jpaApi.em();
        //alternative @SQLDelete(sql = "UPDATE FavoritesList SET deleteToken = ... WHERE id=?")
        em.createQuery("update FavoritesList set deleteToken = :token where id=:id")
                .setParameter("token", UUID.randomUUID().toString())
                .setParameter("id", favoritesList.getId())
                .executeUpdate();

        return favoritesList;
    }
}
