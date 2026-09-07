import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TripService } from '../../core/services/trip.service';
import { TranslationService } from '../../core/services/translation.service';
import { Station, Trip } from '../../core/models/trip.model';

@Component({
  selector: 'app-trip-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trip-search.component.html',
  styleUrl: './trip-search.component.scss'
})
export class TripSearchComponent implements OnInit {
  stations = signal<Station[]>([]);
  results = signal<Trip[]>([]);
  loading = signal<boolean>(false);
  searched = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  fieldErrors = signal<Record<string, string>>({});

  currentPage = signal<number>(0);
  pageSize = signal<number>(5);
  totalPages = signal<number>(0);
  totalElements = signal<number>(0);
  isFirstPage = signal<boolean>(true);
  isLastPage = signal<boolean>(true);

  origin = '';
  destination = '';
  date = '';
  adults = 1;
  children = 0;

  todayString = '';

  constructor(
    private router: Router,
    private tripService: TripService,
    private translationService: TranslationService
  ) {
    const today = new Date();
    this.todayString = today.toISOString().split('T')[0];
    this.date = this.todayString;
  }

  selectTrip(trip: Trip): void {
    this.router.navigate(['/trips', trip.id, 'seats'], {
      queryParams: {
        passengers: this.totalPassengers,
        adults: this.adults,
        children: this.children
      }
    });
  }

  ngOnInit(): void {
    this.tripService.getStations().subscribe({
      next: (stations) => {
        this.stations.set(stations);
        if (stations.length > 0 && !this.origin) {
          this.origin = stations[0].code;
          if (stations.length > 1 && !this.destination) {
            this.destination = stations[1].code;
          }
        }
      }
    });
  }

  get totalPassengers(): number {
    return this.adults + this.children;
  }

  t(key: string, params?: Record<string, any>): string {
    return this.translationService.t(key, params);
  }

  incrementAdults(): void {
    if (this.adults < 9) this.adults++;
  }

  decrementAdults(): void {
    if (this.adults > 1) this.adults--;
  }

  incrementChildren(): void {
    if (this.children < 9) this.children++;
  }

  decrementChildren(): void {
    if (this.children > 0) this.children--;
  }

  swapStations(): void {
    const temp = this.origin;
    this.origin = this.destination;
    this.destination = temp;
    this.validateStations();
  }

  validateStations(): void {
    if (this.origin && this.destination && this.origin === this.destination) {
      this.errorMessage.set(this.t('search.error.same_stations'));
    } else {
      this.errorMessage.set(null);
    }
  }

  onSearch(): void {
    this.currentPage.set(0);
    this.fetchPage(0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages() && page !== this.currentPage()) {
      this.fetchPage(page);
    }
  }

  private fetchPage(page: number): void {
    this.validateStations();
    if (this.errorMessage()) {
      return;
    }

    if (!this.date) {
      this.errorMessage.set(this.t('search.error.missing_date'));
      return;
    }

    this.fieldErrors.set({});
    this.loading.set(true);
    this.tripService.searchTrips({
      origin: this.origin,
      destination: this.destination,
      date: this.date,
      adults: this.adults,
      children: this.children,
      page: page,
      size: this.pageSize()
    }).subscribe({
      next: (pageResponse) => {
        this.results.set(pageResponse.content);
        this.currentPage.set(pageResponse.page);
        this.totalPages.set(pageResponse.totalPages);
        this.totalElements.set(pageResponse.totalElements);
        this.isFirstPage.set(pageResponse.first);
        this.isLastPage.set(pageResponse.last);
        this.loading.set(false);
        this.searched.set(true);
      },
      error: (err) => {
        this.loading.set(false);
        this.searched.set(true);
        if (err?.status === 400 && Array.isArray(err?.error?.invalidParams)) {
          const errors: Record<string, string> = {};
          err.error.invalidParams.forEach((param: any) => {
            errors[param.name] = this.translationService.t(param.messageKey, { defaultValue: param.message });
          });
          this.fieldErrors.set(errors);
        }
      }
    });
  }

  formatDuration(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return m === 0 ? `${h}h` : `${h}h${m < 10 ? '0' : ''}${m}`;
  }
}
