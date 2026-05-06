import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Driver } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class DriverService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/drivers`;

  list(activeOnly = false): Observable<Driver[]> {
    const params = activeOnly ? new HttpParams().set('active', 'true') : undefined;
    return this.http.get<Driver[]>(this.baseUrl, { params });
  }
}
