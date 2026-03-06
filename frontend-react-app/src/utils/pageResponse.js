const DEFAULT_PAGE = Object.freeze({
    content: [],
    pageNumber: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
});

export const normalizePageResponse = (payload) => {
    if (Array.isArray(payload)) {
        return {
            ...DEFAULT_PAGE,
            content: payload,
            pageSize: payload.length,
            totalElements: payload.length,
            totalPages: payload.length > 0 ? 1 : 0,
        };
    }

    if (payload && Array.isArray(payload.content)) {
        return {
            ...DEFAULT_PAGE,
            ...payload,
            content: payload.content,
        };
    }

    return { ...DEFAULT_PAGE };
};

