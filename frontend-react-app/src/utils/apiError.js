export class ApiError extends Error {
    constructor({
        message = 'Unexpected API error',
        status = null,
        errors = null,
        timestamp = null,
        method = null,
        url = null,
        originalError = null,
    } = {}) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.errors = errors;
        this.timestamp = timestamp;
        this.method = method;
        this.url = url;
        this.originalError = originalError;
    }
}

export const toApiError = (error) => {
    const responseData = error?.response?.data;
    const requestConfig = error?.config;
    const fallbackMessage = error?.message || 'Unexpected API error';

    return new ApiError({
        message: responseData?.message || fallbackMessage,
        status: error?.response?.status ?? null,
        errors: responseData?.errors || null,
        timestamp: responseData?.timestamp || null,
        method: requestConfig?.method?.toUpperCase?.() || null,
        url: requestConfig?.url || null,
        originalError: error,
    });
};

