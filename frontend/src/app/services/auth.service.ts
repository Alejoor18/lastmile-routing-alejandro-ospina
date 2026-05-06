import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/api.models';

const TOKEN_KEY = 'lastmile_token';
const USER_KEY = 'lastmile_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly userSubject = new BehaviorSubject<User | null>(this.readUser());

  readonly user$ = this.userSubject.asObservable();

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap((response) => this.storeSession(response)),
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, request).pipe(
      tap((response) => this.storeSession(response)),
    );
  }

  me(): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/auth/me`).pipe(
      tap((user) => {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        this.userSubject.next(user);
      }),
    );
  }

  logout(redirect = true): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSubject.next(null);
    if (redirect) {
      void this.router.navigate(['/login']);
    }
  }

  token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  currentUser(): User | null {
    return this.userSubject.value;
  }

  isAuthenticated(): boolean {
    return Boolean(this.token());
  }

  private storeSession(response: AuthResponse): void {
    const user: User = {
      id: response.userId,
      fullName: response.fullName,
      email: response.email,
      role: response.role,
    };
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.userSubject.next(user);
  }

  private readUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as User;
    } catch {
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }
}
