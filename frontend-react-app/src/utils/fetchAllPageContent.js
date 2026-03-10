import { normalizePageResponse } from './pageResponse';

export const fetchAllPageContent = async (fetchPage, baseParams = {}) => {
    const firstResponse = await fetchPage({
        ...baseParams,
        page: 0,
        size: 1,
    });
    const firstPage = normalizePageResponse(firstResponse.data);

    if (firstPage.totalElements <= firstPage.pageSize) {
        return firstPage.content;
    }

    const secondResponse = await fetchPage({
        ...baseParams,
        page: 0,
        size: firstPage.totalElements,
    });
    const secondPage = normalizePageResponse(secondResponse.data);

    return secondPage.content;
};

