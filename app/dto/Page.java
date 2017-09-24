package dto;

import java.util.Collection;

public class Page<T> {
    private final int page;
    private final int pageSize;
    private final int totalPages;
    private final int totalResults;
    private final Collection<T> results;

    public Page(Pagination pagination, int totalResults, Collection<T> results) {
        this.page = pagination.getPage();
        this.pageSize = pagination.getPageSize();
        this.totalResults = totalResults;
        this.totalPages = (int) Math.ceil(totalResults / (double) pagination.getPageSize());
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public Collection<T> getResults() {
        return results;
    }

    public int getPageSize() {
        return pageSize;
    }
}
