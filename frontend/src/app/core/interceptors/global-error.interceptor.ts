import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { TranslationService } from '../services/translation.service';

export const globalErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  const translationService = inject(TranslationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 1. Erreur de règle métier 422 avec format ProblemDetail (RFC 7807)
      if (error.status === 422 && error.error?.errorCode) {
        const errorCode = error.error.errorCode;
        const params = error.error.params;
        const translatedMessage = translationService.t(errorCode, params);
        const title = translationService.t('error.business.title');

        toastService.showWarning(translatedMessage, title);
      } 
      // 2. Erreur de validation de paramètre 400 avec RFC 7807 invalidParams
      else if (error.status === 400 && Array.isArray(error.error?.invalidParams) && error.error.invalidParams.length > 0) {
        const firstInvalid = error.error.invalidParams[0];
        const key = firstInvalid.messageKey;
        const translatedMessage = translationService.t(key, { defaultValue: firstInvalid.message });
        const title = translationService.t('error.generic.title');

        toastService.showError(translatedMessage, title);
      } 
      // 2. Erreur réseau / serveur indisponible
      else if (error.status === 0) {
        toastService.showError('Serveur indisponible ou erreur réseau.', 'Connexion');
      }
      // 3. Erreur 500 serveur
      else if (error.status >= 500) {
        toastService.showError('Une erreur serveur est survenue.', 'Erreur');
      }

      return throwError(() => error);
    })
  );
};
