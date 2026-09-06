import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AppConfig } from '../config/app-config.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private readonly configSignal = signal<AppConfig | null>(null);

  constructor(private http: HttpClient) {}

  async loadConfig(): Promise<void> {
    try {
      const config = await firstValueFrom(
        this.http.get<AppConfig>(`/config.json?v=${Date.now()}`)
      );
      if (config) {
        this.configSignal.set(config);
      }
    } catch (err) {
      console.error('Failed to load /config.json at runtime:', err);
      throw err;
    }
  }

  get config(): AppConfig | null {
    return this.configSignal();
  }

  get apiUrl(): string {
    return this.configSignal()?.apiUrl ?? '';
  }
}
