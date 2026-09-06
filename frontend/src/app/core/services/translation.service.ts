import { Injectable, signal, computed } from '@angular/core';

export type Language = 'fr' | 'en';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {
  private currentLang = signal<Language>('fr');

  readonly currentLanguage = computed(() => this.currentLang());

  private readonly translations: Record<Language, Record<string, string>> = {
    fr: {
      'brand.name': 'NewGen TGV',
      'brand.tagline': 'L’excellence de la grande vitesse sur l’axe Ouest',
      'search.title': 'Où souhaitez-vous voyager ?',
      'search.origin': 'Gare de départ',
      'search.destination': 'Gare d’arrivée',
      'search.origin.placeholder': 'Sélectionnez la gare de départ',
      'search.destination.placeholder': 'Sélectionnez la gare d’arrivée',
      'search.date': 'Date aller',
      'search.passengers': 'Voyageurs',
      'search.adults': 'Adultes',
      'search.adults.hint': '12 ans et +',
      'search.children': 'Enfants',
      'search.children.hint': '0 - 11 ans',
      'search.button': 'Rechercher un trajet',
      'search.searching': 'Recherche des meilleurs trajets...',
      'search.error.same_stations': 'La gare de départ et la gare d’arrivée doivent être différentes.',
      'search.error.missing_date': 'Veuillez sélectionner une date aller.',
      'search.error.no_passengers': 'Au moins un adulte ou enfant doit voyager.',
      'results.title': 'Trains disponibles pour votre voyage',
      'results.empty': 'Aucun train disponible pour cette date et cet itinéraire.',
      'results.direct': 'Direct',
      'results.from_price': 'dès',
      'results.seats_standard': 'places 2nde',
      'results.seats_first': 'places 1ère',
      'results.select_btn': 'Choisir ce train',
      'results.duration': 'Durée',
      'swap.tooltip': 'Inverser le sens du trajet',
      'nav.home': 'Accueil',
      'nav.contact': 'Support'
    },
    en: {
      'brand.name': 'NewGen TGV',
      'brand.tagline': 'High-speed rail excellence for the Western corridor',
      'search.title': 'Where would you like to travel?',
      'search.origin': 'Departure station',
      'search.destination': 'Arrival station',
      'search.origin.placeholder': 'Select departure station',
      'search.destination.placeholder': 'Select arrival station',
      'search.date': 'Departure date',
      'search.passengers': 'Passengers',
      'search.adults': 'Adults',
      'search.adults.hint': '12+ yrs',
      'search.children': 'Children',
      'search.children.hint': '0 - 11 yrs',
      'search.button': 'Search trips',
      'search.searching': 'Searching best available trains...',
      'search.error.same_stations': 'Departure and arrival stations must be different.',
      'search.error.missing_date': 'Please pick a travel date.',
      'search.error.no_passengers': 'At least one adult or child is required.',
      'results.title': 'Available trains for your journey',
      'results.empty': 'No trains found for this route and date.',
      'results.direct': 'Non-stop',
      'results.from_price': 'from',
      'results.seats_standard': 'standard seats',
      'results.seats_first': '1st class seats',
      'results.select_btn': 'Select train',
      'results.duration': 'Duration',
      'swap.tooltip': 'Swap departure & arrival',
      'nav.home': 'Home',
      'nav.contact': 'Support'
    }
  };

  setLanguage(lang: Language): void {
    this.currentLang.set(lang);
  }

  toggleLanguage(): void {
    this.currentLang.update(lang => (lang === 'fr' ? 'en' : 'fr'));
  }

  t(key: string): string {
    const lang = this.currentLang();
    return this.translations[lang][key] || key;
  }
}
