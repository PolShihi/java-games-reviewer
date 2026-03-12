import { ApiError } from '../http/api-error';

export const getApiErrorMessage = (
  error: unknown,
  fallbackMessage: string
): string => {
  const apiError = error as ApiError | undefined;
  const validationMessage =
    apiError?.errors && typeof apiError.errors === 'object'
      ? Object.values(apiError.errors)[0]
      : null;

  return (
    validationMessage ||
    apiError?.message ||
    fallbackMessage
  );
};
