import { Station } from './trip.model';

export type CoachClass = 'FIRST' | 'STANDARD';
export type SeatPosition = 'WINDOW' | 'AISLE' | 'SOLO' | 'DUO';
export type SeatStatus = 'AVAILABLE' | 'LOCKED' | 'BOOKED';

export interface Seat {
  id: number;
  seatNumber: number;
  seatCode: string;
  coachNumber: number;
  coachClass: CoachClass;
  position: SeatPosition;
  status: SeatStatus;
  price: number;
}

export interface CoachSeats {
  coachNumber: number;
  coachClass: CoachClass;
  price: number;
  totalSeats: number;
  availableSeatsCount: number;
  seats: Seat[];
}

export interface SeatPricing {
  standard: number;
  first: number;
}

export interface TripSeatsResponse {
  tripId: number;
  trainNumber: string;
  departureStation: Station;
  arrivalStation: Station;
  departureTime: string;
  arrivalTime: string;
  pricing: SeatPricing;
  totalSeats: number;
  availableSeats: number;
  coaches: CoachSeats[];
}
