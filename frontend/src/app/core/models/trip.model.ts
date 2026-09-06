export interface Station {
  code: string;
  name: string;
  city: string;
}

export interface AvailableSeats {
  standardClass: number;
  firstClass: number;
}

export interface Trip {
  id: number;
  trainNumber: string;
  departureStation: Station;
  arrivalStation: Station;
  departureTime: string; // ISO-8601
  arrivalTime: string;   // ISO-8601
  durationMinutes: number;
  startingPrice: number;
  availableSeats: AvailableSeats;
}

export interface TripPageResponse {
  content: Trip[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface TripSearchParams {
  origin: string;
  destination: string;
  date: string; // YYYY-MM-DD
  adults: number;
  children: number;
  page?: number;
  size?: number;
}
