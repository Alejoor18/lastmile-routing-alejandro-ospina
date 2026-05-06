import { DecimalPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { Delivery, DeliveryStatus, RoutePlan } from '../../models/api.models';
import { DeliveryService } from '../../services/delivery.service';
import { HealthService } from '../../services/health.service';
import { RouteService } from '../../services/route.service';

interface Kpi {
  label: string;
  value: string | number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DecimalPipe, NgFor, NgIf, RouterLink],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  private readonly deliveryService = inject(DeliveryService);
  private readonly routeService = inject(RouteService);
  private readonly healthService = inject(HealthService);

  protected loading = true;
  protected backendUp = false;
  protected error = '';
  protected kpis: Kpi[] = [];
  protected lastRoute: RoutePlan | null = null;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    forkJoin({
      health: this.healthService.check().pipe(catchError(() => of(null))),
      deliveries: this.deliveryService.list(),
      routes: this.routeService.list(),
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: ({ health, deliveries, routes }) => {
          this.backendUp = health?.status === 'UP';
          this.lastRoute = routes[0] ?? null;
          this.kpis = this.buildKpis(deliveries, routes);
        },
        error: (error) => {
          this.error = error.error?.message ?? 'No se pudo cargar el dashboard';
          this.backendUp = false;
        },
      });
  }

  private buildKpis(deliveries: Delivery[], routes: RoutePlan[]): Kpi[] {
    const byStatus = (status: DeliveryStatus) => deliveries.filter((delivery) => delivery.status === status).length;
    return [
      { label: 'Total entregas', value: deliveries.length },
      { label: 'Pendientes', value: byStatus(DeliveryStatus.PENDING) },
      { label: 'Asignadas', value: byStatus(DeliveryStatus.ASSIGNED) },
      { label: 'En transito', value: byStatus(DeliveryStatus.IN_TRANSIT) },
      { label: 'Entregadas', value: byStatus(DeliveryStatus.DELIVERED) },
      { label: 'Total rutas', value: routes.length },
      { label: 'Distancia ultima ruta', value: routes[0] ? `${routes[0].totalDistanceKm.toFixed(2)} km` : '0 km' },
    ];
  }
}
