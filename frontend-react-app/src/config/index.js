const config = {
  api: {
    baseUrl: import.meta.env.VITE_API_URL || 'http://localhost:8088/api/v1',
    timeout: 5000,
  },
  app: {
    name: import.meta.env.VITE_APP_NAME || 'Games reviewer app',
    isDev: import.meta.env.DEV,
    enableLogs: import.meta.env.VITE_ENABLE_LOGS === 'true',
  },
};

export default config;