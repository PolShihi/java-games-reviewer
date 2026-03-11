export const appConfig = {
  api: {
    baseUrl: 'http://localhost:8088/api/v1',
    timeoutMs: 5000,
  },
  app: {
    name: 'Games reviewer app',
    enableLogs: false,
  },
};

export type AppConfig = typeof appConfig;
