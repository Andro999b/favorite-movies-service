package utils;

import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public final class JpaUtils {

    public static <T> Optional<T> uniqueResult(TypedQuery<T> query){
        List<T> results = query.getResultList();
        if (results.isEmpty()) return Optional.empty();
        else if (results.size() == 1) return Optional.of(results.get(0));
        throw new NonUniqueResultException();
    }

    private JpaUtils() {}
}
