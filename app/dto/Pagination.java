package dto;

public class Pagination {
    private final int page;
    private final int pageSize;

    public Pagination(int page, int pageSize) {
        this.page = page < 0 ? 1 : page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return (page - 1) * pageSize;
    }
}
