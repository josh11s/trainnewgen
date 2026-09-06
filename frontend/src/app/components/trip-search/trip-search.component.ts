import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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

  origin = '';
  destination = '';
  date = '';
  adults = 1;
  children = 0;

  todayString = '';

  constructor(
    private tripService: TripService,
    private translationService: TranslationService
  ) {
    const today = new Date();
    this.todayString = today.toISOString().split('T')[0];
    this.date = this.todayString;
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

  t(key: string): string {
    return this.translationService.t(key);
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
    this.validateStations();
    if (this.errorMessage()) {
      return;
    }

    if (!this.date) {
      this.errorMessage.set(this.t('search.error.missing_date'));
      return;
    }

    this.loading.set(true);
    this.tripService.searchTrips({
      origin: this.origin,
      destination: this.destination,
      date: this.date,
      adults: this.adults,
      children: this.children
    }).subscribe({
      next: (trips) => {
        this.results.set(trips);
        this.loading.set(false);
        this.searched.set(true);
      },
      error: () => {
        this.loading.set(false);
        this.searched.set(true);
      }
    });
  }

  formatDuration(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return m === 0 ? `${h}h` : `${h}h${m < 10 ? '0' : ''}${m}`;
  }
}
