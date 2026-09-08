import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TripService } from '../../core/services/trip.service';
import { TranslationService } from '../../core/services/translation.service';
import { ToastService } from '../../core/services/toast.service';
import { TripSeatsResponse, CoachSeats, Seat } from '../../core/models/seat.model';

export interface CoachRow {
  rowNumber: number;
  leftSeats: Seat[];
  rightSeats: Seat[];
}

@Component({
  selector: 'app-seat-selection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seat-selection.component.html',
  styleUrl: './seat-selection.component.scss'
})
export class SeatSelectionComponent implements OnInit {
  tripId = signal<number | null>(null);
  passengersQuota = signal<number>(1);
  tripData = signal<TripSeatsResponse | null>(null);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);
  selectedSeats = signal<Seat[]>([]);
  paying = signal<boolean>(false);
  conflictError = signal<string | null>(null);
  paymentConfirmed = signal<boolean>(false);
  activeCoachTab = signal<number>(0); // 0: All coaches, 1: Coach 1, 2: Coach 2

  totalAmount = computed(() => {
    return this.selectedSeats().reduce((sum, s) => sum + s.price, 0);
  });

  isQuotaFulfilled = computed(() => {
    return this.selectedSeats().length >= this.passengersQuota();
  });

  getCoachRows(coach: CoachSeats): CoachRow[] {
    const rows: CoachRow[] = [];
    const seats = coach.seats || [];
    for (let r = 0; r < 6; r++) {
      const startIndex = r * 5;
      rows.push({
        rowNumber: r + 1,
        leftSeats: seats.slice(startIndex, startIndex + 3),
        rightSeats: seats.slice(startIndex + 3, startIndex + 5)
      });
    }
    return rows;
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tripService: TripService,
    private translationService: TranslationService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('tripId');
      if (idParam) {
        const id = parseInt(idParam, 10);
        this.tripId.set(id);
        this.loadSeats(id);
      }
    });

    this.route.queryParamMap.subscribe(queryParams => {
      const passParam = queryParams.get('passengers');
      if (passParam) {
        const p = parseInt(passParam, 10);
        if (!isNaN(p) && p > 0) {
          this.passengersQuota.set(p);
        }
      }
    });
  }

  loadSeats(tripId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.tripService.getTripSeats(tripId).subscribe({
      next: (data) => {
        this.tripData.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.detail || 'Erreur lors du chargement des sièges');
        this.toastService.showError(this.error()!);
      }
    });
  }

  t(key: string, params?: Record<string, any>): string {
    return this.translationService.t(key, params);
  }

  isSeatSelected(seatId: number): boolean {
    return this.selectedSeats().some(s => s.id === seatId);
  }

  toggleSeat(seat: Seat): void {
    if (seat.status !== 'AVAILABLE') {
      return;
    }

    const current = this.selectedSeats();
    const index = current.findIndex(s => s.id === seat.id);

    if (index >= 0) {
      // Unselect
      this.selectedSeats.set(current.filter(s => s.id !== seat.id));
    } else {
      // Check quota
      if (current.length >= this.passengersQuota()) {
        if (this.passengersQuota() === 1) {
          // If only 1 passenger, replace the selected seat smoothly
          this.selectedSeats.set([seat]);
          return;
        }
        this.toastService.show({
          type: 'info',
          message: this.t('seats.max_reached', { max: this.passengersQuota() })
        });
        return;
      }
      this.selectedSeats.set([...current, seat]);
    }
  }

  onPay(): void {
    if (this.selectedSeats().length === 0 || !this.tripId() || this.paying()) {
      return;
    }

    this.paying.set(true);
    this.conflictError.set(null);

    const tripId = this.tripId()!;
    const seatIds = this.selectedSeats().map(s => s.id);

    this.tripService.paySeats(tripId, seatIds).subscribe({
      next: (_res) => {
        this.paying.set(false);
        this.paymentConfirmed.set(true);
        const seatCodes = this.selectedSeats().map(s => s.seatCode).join(', ');
        const total = this.totalAmount().toFixed(2);
        this.toastService.showSuccess(
          this.t('seats.pay_success', { seats: seatCodes, total })
        );
      },
      error: (err) => {
        this.paying.set(false);
        if (err.status === 409) {
          const errorCode = err?.error?.errorCode;
          const params = err?.error?.params;
          const unavailableSeats = err?.error?.unavailableSeats as Array<{ id: number; seatCode: string; status: 'LOCKED' | 'BOOKED' | 'AVAILABLE' }>;

          // 1. Immediately disable those seats in the current train map
          if (Array.isArray(unavailableSeats) && this.tripData()) {
            const currentData = this.tripData()!;
            const unavailableMap = new Map(unavailableSeats.map(u => [u.id, u.status]));

            currentData.coaches.forEach(coach => {
              coach.seats.forEach(s => {
                if (unavailableMap.has(s.id)) {
                  s.status = unavailableMap.get(s.id)!;
                }
              });
            });
            this.tripData.set({ ...currentData });
          }

          // 2. Clear current selection
          this.selectedSeats.set([]);

          // 3. Choose singular vs plural translation key
          let translationKey = errorCode || 'error.business.seat_already_reserved';
          if (params?.count > 1 || (params?.seats && params.seats.includes(','))) {
            translationKey = 'error.business.seats_already_reserved';
          }

          const detailMsg = this.t(translationKey, params);
          this.conflictError.set(detailMsg);

          // 4. Reload seats to sync complete train state with backend
          this.loadSeats(tripId);
        } else {
          const errMsg =
            err?.error?.detail ||
            err?.error?.message ||
            'Une erreur est survenue lors de la réservation.';
          this.toastService.showError(errMsg);
        }
      }
    });
  }

  closeConflictModal(): void {
    this.conflictError.set(null);
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
