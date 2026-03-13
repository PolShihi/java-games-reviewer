const runtimeConfig = (globalThis as { __APP_CONFIG__?: { API_BASE_URL?: string; ENABLE_LOGS?: string } })
  .__APP_CONFIG__;

const apiBaseUrl = runtimeConfig?.API_BASE_URL || 'http://localhost:8088/api/v1';
const enableLogs = runtimeConfig?.ENABLE_LOGS === 'true';

export const appConfig = {
  api: {
    baseUrl: apiBaseUrl,
    timeoutMs: 5000,
  },
  app: {
    name: 'Games reviewer app',
    enableLogs,
  },
};

export type AppConfig = typeof appConfig;
