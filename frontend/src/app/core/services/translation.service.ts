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
      'validation.date.type': 'La date sélectionnée est invalide.',
      'validation.date.required': 'La date aller est obligatoire.',
      'validation.adults.min': 'Au moins 1 adulte doit être sélectionné.',
      'validation.adults.required': 'Le nombre d’adultes est requis.',
      'validation.children.min': 'Le nombre d’enfants ne peut pas être négatif.',
      'error.business.max_passengers_exceeded': 'La réservation est limitée à un maximum de {{maxAllowed}} voyageurs par trajet.',
      'error.business.seat_already_reserved': 'La place {{seats}} a déjà été réservée (ou est en cours de réservation) par un autre voyageur. Veuillez en choisir une autre.',
      'error.business.seats_already_reserved': 'Les places {{seats}} ont déjà été réservées (ou sont en cours de réservation) par d’autres voyageurs. Veuillez en choisir d’autres.',
      'error.generic.title': 'Erreur de saisie',
      'error.business.title': 'Attention :',
      'results.title': 'Trains disponibles pour votre voyage',
      'results.empty': 'Aucun train disponible pour cette date et cet itinéraire.',
      'results.direct': 'Direct',
      'results.from_price': 'dès',
      'results.seats_standard': 'places 2nde',
      'results.seats_first': 'places 1ère',
      'results.select_btn': 'Choisir ce train',
      'results.duration': 'Durée',
      'pagination.prev': 'Page précédente',
      'pagination.next': 'Page suivante',
      'pagination.page_of': 'Page {{page}} sur {{totalPages}} ({{totalElements}} trains)',
      'swap.tooltip': 'Inverser le sens du trajet',
      'nav.home': 'Accueil',
      'nav.contact': 'Support',
      'seats.back': '← Retour aux résultats',
      'seats.title': 'Choix de vos places à bord',
      'seats.subtitle': 'Sélectionnez vos sièges sur le plan du train',
      'seats.required_count': 'Places à sélectionner : {{selected}} / {{total}} voyageur(s)',
      'seats.coach1_title': 'Voiture 1 – 1ère Classe',
      'seats.coach2_title': 'Voiture 2 – 2nde Classe',
      'seats.first_class': '1ère Classe',
      'seats.standard_class': '2nde Classe',
      'seats.legend.available': 'Disponible',
      'seats.legend.selected': 'Votre sélection',
      'seats.legend.occupied': 'Occupé / Indisponible',
      'seats.selected_summary': 'Sièges choisis :',
      'seats.none_selected': 'Aucun siège sélectionné',
      'seats.total_amount': 'Total',
      'seats.pay_btn': 'Payer',
      'seats.pay_success': 'Paiement validé avec succès pour les sièges : {{seats}} (Total : {{total}} €) !',
      'seats.max_reached': 'Vous avez déjà sélectionné vos {{max}} places.',
      'seats.coach_deck': 'Rame TGV Ouest',
      'seats.coach_label': 'Voiture',
      'seats.locomotive': 'Avant du train'
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
      'validation.date.type': 'Selected date is invalid.',
      'validation.date.required': 'Departure date is required.',
      'validation.adults.min': 'At least 1 adult passenger is required.',
      'validation.adults.required': 'Number of adults is required.',
      'validation.children.min': 'Number of children cannot be negative.',
      'error.business.max_passengers_exceeded': 'Bookings cannot exceed {{maxAllowed}} passengers per trip.',
      'error.business.seat_already_reserved': 'Seat {{seats}} has already been booked (or is being booked) by another traveler. Please choose another one.',
      'error.business.seats_already_reserved': 'Seats {{seats}} have already been booked (or are being booked) by other travelers. Please choose other seats.',
      'error.generic.title': 'Input Error',
      'error.business.title': 'Warning',
      'results.title': 'Available trains for your journey',
      'results.empty': 'No trains found for this route and date.',
      'results.direct': 'Non-stop',
      'results.from_price': 'from',
      'results.seats_standard': 'standard seats',
      'results.seats_first': '1st class seats',
      'results.select_btn': 'Select train',
      'results.duration': 'Duration',
      'pagination.prev': 'Previous page',
      'pagination.next': 'Next page',
      'pagination.page_of': 'Page {{page}} of {{totalPages}} ({{totalElements}} trains)',
      'swap.tooltip': 'Swap departure & arrival',
      'nav.home': 'Home',
      'nav.contact': 'Support',
      'seats.back': '← Back to search results',
      'seats.title': 'Choose your seats on board',
      'seats.subtitle': 'Select your seats on the train map',
      'seats.required_count': 'Seats to pick: {{selected}} / {{total}} passenger(s)',
      'seats.coach1_title': 'Coach 1 – 1st Class',
      'seats.coach2_title': 'Coach 2 – Standard Class',
      'seats.first_class': '1st Class',
      'seats.standard_class': 'Standard Class',
      'seats.legend.available': 'Available',
      'seats.legend.selected': 'Your selection',
      'seats.legend.occupied': 'Occupied / Unavailable',
      'seats.selected_summary': 'Selected seats:',
      'seats.none_selected': 'No seat selected',
      'seats.total_amount': 'Total',
      'seats.pay_btn': 'Pay',
      'seats.pay_success': 'Payment confirmed successfully for seats: {{seats}} (Total: {{total}} €)!',
      'seats.max_reached': 'You have already selected all your {{max}} seats.',
      'seats.coach_deck': 'TGV West Trainset',
      'seats.coach_label': 'Coach',
      'seats.locomotive': 'Front Engine'
    }
  };

  setLanguage(lang: Language): void {
    this.currentLang.set(lang);
  }

  toggleLanguage(): void {
    this.currentLang.update(lang => (lang === 'fr' ? 'en' : 'fr'));
  }

  t(key: string, params?: Record<string, any>): string {
    const lang = this.currentLang();
    let text = this.translations[lang][key] || key;
    if (params) {
      Object.keys(params).forEach((p) => {
        text = text.replace(new RegExp(`{{${p}}}`, 'g'), String(params[p]));
      });
    }
    return text;
  }
}
