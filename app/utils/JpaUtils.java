package utils;

import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public final class JpaUtils {
    //according to SQL95 standard NULL is non unique value, and can be used in unique indexes
    public final static String EMPTY_DELETE_TOKEN = "none";

    public static <T> Optional<T> uniqueResult(TypedQuery<T> query){
        List<T> results = query.getResultList();
        if (results.isEmpty()) return Optional.empty();
        else if (results.size() == 1) return Optional.of(results.get(0));
        throw new NonUniqueResultException();
    }

    private JpaUtils() {}
}
