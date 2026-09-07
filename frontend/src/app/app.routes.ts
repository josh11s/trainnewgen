import { Routes } from '@angular/router';
import { TripSearchComponent } from './components/trip-search/trip-search.component';
import { SeatSelectionComponent } from './components/seat-selection/seat-selection.component';

export const routes: Routes = [
  { path: '', component: TripSearchComponent },
  { path: 'trips/:tripId/seats', component: SeatSelectionComponent },
  { path: '**', redirectTo: '' }
];
