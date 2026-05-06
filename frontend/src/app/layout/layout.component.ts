import { AsyncPipe, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [AsyncPipe, NgIf, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './layout.component.html',
})
export class LayoutComponent {
  protected readonly authService = inject(AuthService);
  protected readonly user$ = this.authService.user$;

  logout(): void {
    this.authService.logout();
  }
}
