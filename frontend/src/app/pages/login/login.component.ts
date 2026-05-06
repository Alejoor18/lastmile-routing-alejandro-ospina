import { Component, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected loading = false;
  protected error = '';
  protected readonly form = this.fb.nonNullable.group({
    email: ['admin@lastmile.test', [Validators.required, Validators.email]],
    password: ['Admin123*', [Validators.required]],
  });

  submit(): void {
    this.error = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => void this.router.navigate(['/dashboard']),
      error: (error) => {
        this.error = error.error?.message ?? 'No se pudo iniciar sesion';
        this.loading = false;
      },
    });
  }
}
