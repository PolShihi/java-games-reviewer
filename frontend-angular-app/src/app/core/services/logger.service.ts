import { Injectable } from '@angular/core';
import { appConfig } from '../config/app-config';

@Injectable({ providedIn: 'root' })
export class LoggerService {
  private readonly enabled = appConfig.app.enableLogs;

  debug(...messages: unknown[]) {
    this.logWithLevel('DEBUG', ...messages);
  }

  info(...messages: unknown[]) {
    this.logWithLevel('INFO', ...messages);
  }

  warn(...messages: unknown[]) {
    this.logWithLevel('WARN', ...messages);
  }

  error(...messages: unknown[]) {
    this.logWithLevel('ERROR', ...messages);
  }

  private logWithLevel(level: string, ...messages: unknown[]) {
    if (!this.enabled && level !== 'ERROR') {
      return;
    }

    const timestamp = new Date().toLocaleTimeString();
    const prefix = `[${timestamp}] [${level}]`;
    // eslint-disable-next-line no-console
    console[level === 'ERROR' ? 'error' : level === 'WARN' ? 'warn' : 'log'](prefix, ...messages);
  }
}
