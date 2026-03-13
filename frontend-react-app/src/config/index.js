const runtimeConfig = globalThis?.__APP_CONFIG__ || {};

const config = {
  api: {
    baseUrl: runtimeConfig.API_BASE_URL || import.meta.env.VITE_API_URL || 'http://localhost:8088/api/v1',
    timeout: 5000,
  },
  app: {
    name: runtimeConfig.APP_NAME || import.meta.env.VITE_APP_NAME || 'Games reviewer app',
    isDev: import.meta.env.DEV,
    enableLogs: (runtimeConfig.ENABLE_LOGS || import.meta.env.VITE_ENABLE_LOGS) === 'true',
  },
};

export default config;
