import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RouteOptimizeRequest, RoutePlan, RouteStatus } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class RouteService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/routes`;

  list(): Observable<RoutePlan[]> {
    return this.http.get<RoutePlan[]>(this.baseUrl);
  }

  optimize(request: RouteOptimizeRequest): Observable<RoutePlan> {
    return this.http.post<RoutePlan>(`${this.baseUrl}/optimize`, request);
  }

  updateStatus(id: number, status: RouteStatus): Observable<RoutePlan> {
    return this.http.patch<RoutePlan>(`${this.baseUrl}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
