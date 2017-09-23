package utils;

import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.Links;
import dto.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theoryinpractise.halbuilder5.Link.SELF;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

public final class HalLinks {

    public static Link favorites() {
        return Links.create("favorites", "/favorites");
    }

    public static List<Link> favoritesPagination(Page<?> page) {
        String template = "/favorites?page={page}&pageSize={pageSize}";
        return createPaginationLinks(template, page, emptyMap());
    }

    public static Link favoritesList(Object listId) {
        return Links.create(SELF, "/favorites/{listId}", singletonMap("listId", String.valueOf(listId)));
    }

    public static Link favoritesListMovies(Object listId) {
        return Links.create("movies", "/favorites/{listId}/movies", singletonMap("listId", String.valueOf(listId)));
    }

    public static List<Link> favoritesListMoviesPagination(Object listId, Page<?> page) {
        String template = "/favorites/{listId}/movies?page={page}&pageSize={pageSize}";
        return createPaginationLinks(template, page, singletonMap("listId", String.valueOf(listId)));
    }

    public static Link movie(Object listId, Object movieId) {
        Map<String, String> properties = new HashMap<>();
        properties.put("listId", String.valueOf(listId));
        properties.put("movieId", String.valueOf(movieId));

        return Links.create(SELF, "/favorites/{listId}/movies/{movieId}", properties);
    }

    private static List<Link> createPaginationLinks(String template, Page<?> page, Map<String, String> properties) {
        List<Link> links = new ArrayList<>();

        if(page.getPage() < page.getTotalPages()) {
            int nextPage = page.getPage() + 1;

            Map<String, String> templateProperties = new HashMap<>(properties);
            templateProperties.put("page", String.valueOf(nextPage));
            templateProperties.put("pageSize", String.valueOf(page.getPageSize()));

            links.add(Links.create("next", template, templateProperties));
        }

        if(page.getPage() > 1) {
            int nextPage = page.getPage() - 1;

            Map<String, String> templateProperties = new HashMap<>(properties);
            templateProperties.put("page", String.valueOf(nextPage));
            templateProperties.put("pageSize", String.valueOf(page.getPageSize()));

            links.add(Links.create("prev", template, templateProperties));
        }

        return links;
    }

    private HalLinks() {}
}
