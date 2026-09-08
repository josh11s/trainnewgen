import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';
import { Station, TripPageResponse, TripSearchParams } from '../models/trip.model';
import { TripSeatsResponse } from '../models/seat.model';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})
export class TripService {
  private readonly defaultStations: Station[] = [];

  constructor(
    private http: HttpClient,
    private configService: ConfigService
  ) {}

  private get baseUrl(): string {
    return this.configService.apiUrl;
  }

  getStations(): Observable<Station[]> {
    return this.http.get<Station[]>(`${this.baseUrl}/stations`).pipe(
      catchError((err) => {
        console.warn('Failed to load stations from backend:', err);
        return of(this.defaultStations);
      })
    );
  }

  searchTrips(params: TripSearchParams): Observable<TripPageResponse> {
    let httpParams = new HttpParams()
      .set('origin', params.origin)
      .set('destination', params.destination)
      .set('date', params.date)
      .set('adults', params.adults.toString())
      .set('children', params.children.toString())
      .set('page', (params.page ?? 0).toString())
      .set('size', (params.size ?? 5).toString());

    return this.http.get<TripPageResponse>(`${this.baseUrl}/trips/search`, { params: httpParams }).pipe(
      catchError((err) => {
        return throwError(() => err);
      })
    );
  }

  getTripSeats(tripId: number): Observable<TripSeatsResponse> {
    return this.http.get<TripSeatsResponse>(`${this.baseUrl}/trips/${tripId}/seats`).pipe(
      catchError((err) => {
        return throwError(() => err);
      })
    );
  }

  paySeats(tripId: number, seatIds: number[]): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/trips/${tripId}/pay`, { seatIds });
  }
}
