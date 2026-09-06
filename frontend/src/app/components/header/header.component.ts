import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslationService } from '../../core/services/translation.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  constructor(private translationService: TranslationService) {}

  currentLang() {
    return this.translationService.currentLanguage();
  }

  toggleLanguage() {
    this.translationService.toggleLanguage();
  }

  t(key: string): string {
    return this.translationService.t(key);
  }
}
