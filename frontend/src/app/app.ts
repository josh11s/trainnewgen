import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { TripSearchComponent } from './components/trip-search/trip-search.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HeaderComponent, TripSearchComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {}
