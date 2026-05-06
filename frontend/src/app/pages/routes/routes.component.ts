import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DeliveryPriority, RoutePlan, RouteStatus } from '../../models/api.models';
import { RouteService } from '../../services/route.service';

@Component({
  selector: 'app-routes',
  standalone: true,
  imports: [DatePipe, DecimalPipe, FormsModule, NgFor, NgIf],
  templateUrl: './routes.component.html',
})
export class RoutesComponent implements OnInit {
  private readonly routeService = inject(RouteService);

  protected readonly statuses = Object.values(RouteStatus);
  protected routes: RoutePlan[] = [];
  protected selectedRoute: RoutePlan | null = null;
  protected loading = false;
  protected message = '';
  protected error = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.routeService.list().subscribe({
      next: (routes) => {
        this.routes = routes;
        this.selectedRoute = routes.find((route) => route.id === this.selectedRoute?.id) ?? routes[0] ?? null;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message ?? 'No se pudieron cargar las rutas';
        this.loading = false;
      },
    });
  }

  select(route: RoutePlan): void {
    this.selectedRoute = route;
  }

  updateStatus(route: RoutePlan, status: string): void {
    this.routeService.updateStatus(route.id, status as RouteStatus).subscribe({
      next: (updated) => {
        this.message = 'Estado de ruta actualizado';
        this.selectedRoute = updated;
        this.load();
      },
      error: (error) => (this.error = error.error?.message ?? 'No se pudo cambiar el estado'),
    });
  }

  remove(route: RoutePlan): void {
    this.routeService.delete(route.id).subscribe({
      next: () => {
        this.message = 'Ruta eliminada';
        this.selectedRoute = null;
        this.load();
      },
      error: (error) => (this.error = error.error?.message ?? 'No se pudo eliminar la ruta'),
    });
  }

  priorityClass(priority: DeliveryPriority): string {
    return `priority-${priority.toLowerCase()}`;
  }
}
