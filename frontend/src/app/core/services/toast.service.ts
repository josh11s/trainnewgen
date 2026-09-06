import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: string;
  type: 'error' | 'warning' | 'info' | 'success';
  title?: string;
  message: string;
  durationMs?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastsSignal = signal<Toast[]>([]);
  readonly toasts = this.toastsSignal.asReadonly();

  show(toast: Omit<Toast, 'id'>): void {
    const id = Math.random().toString(36).substring(2, 9);
    const newToast: Toast = { ...toast, id, durationMs: toast.durationMs ?? 6000 };

    this.toastsSignal.update((current) => [...current, newToast]);

    if (newToast.durationMs && newToast.durationMs > 0) {
      setTimeout(() => {
        this.dismiss(id);
      }, newToast.durationMs);
    }
  }

  showError(message: string, title?: string): void {
    this.show({ type: 'error', message, title });
  }

  showWarning(message: string, title?: string): void {
    this.show({ type: 'warning', message, title });
  }

  showSuccess(message: string, title?: string): void {
    this.show({ type: 'success', message, title });
  }

  dismiss(id: string): void {
    this.toastsSignal.update((current) => current.filter((t) => t.id !== id));
  }
}
