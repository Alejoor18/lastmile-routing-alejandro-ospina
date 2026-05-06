import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BackendHealth } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly http = inject(HttpClient);

  check(): Observable<BackendHealth> {
    return this.http.get<BackendHealth>(`${environment.apiUrl}/health`);
  }
}
