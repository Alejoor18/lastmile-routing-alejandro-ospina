import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Delivery, DeliveryPriority, DeliveryStatus, Driver, RoutePlan } from '../../models/api.models';
import { DeliveryService } from '../../services/delivery.service';
import { DriverService } from '../../services/driver.service';
import { RouteService } from '../../services/route.service';

interface Segment {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

interface Point {
  x: number;
  y: number;
  label: string;
  priority?: DeliveryPriority;
}

interface RawPoint {
  lat: number;
  lng: number;
  label: string;
  priority?: DeliveryPriority;
}

@Component({
  selector: 'app-optimizer',
  standalone: true,
  imports: [DatePipe, DecimalPipe, NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './optimizer.component.html',
})
export class OptimizerComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly driverService = inject(DriverService);
  private readonly deliveryService = inject(DeliveryService);
  private readonly routeService = inject(RouteService);

  protected drivers: Driver[] = [];
  protected pendingDeliveries: Delivery[] = [];
  protected selectedIds = new Set<number>();
  protected result: RoutePlan | null = null;
  protected loading = false;
  protected optimizing = false;
  protected error = '';
  protected message = '';

  protected readonly form = this.fb.nonNullable.group({
    driverId: [0, [Validators.required, Validators.min(1)]],
    warehouseLat: [6.2442, [Validators.required]],
    warehouseLng: [-75.5812, [Validators.required]],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    forkJoin({
      drivers: this.driverService.list(true),
      deliveries: this.deliveryService.list({ status: DeliveryStatus.PENDING }),
    }).subscribe({
      next: ({ drivers, deliveries }) => {
        this.drivers = drivers;
        this.pendingDeliveries = deliveries;
        this.selectedIds = new Set(deliveries.map((delivery) => delivery.id));
        if (drivers.length > 0 && this.form.controls.driverId.value === 0) {
          this.form.patchValue({ driverId: drivers[0].id });
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message ?? 'No se pudieron cargar datos para optimizar';
        this.loading = false;
      },
    });
  }

  toggleDelivery(id: number, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedIds.add(id);
    } else {
      this.selectedIds.delete(id);
    }
  }

  optimize(): void {
    this.error = '';
    this.message = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.optimizing = true;
    this.routeService
      .optimize({
        driverId: Number(value.driverId),
        warehouseLat: Number(value.warehouseLat),
        warehouseLng: Number(value.warehouseLng),
        deliveryIds: Array.from(this.selectedIds),
      })
      .subscribe({
        next: (route) => {
          this.result = route;
          this.message = 'Ruta optimizada y persistida';
          this.optimizing = false;
          this.load();
        },
        error: (error) => {
          this.error = error.error?.message ?? 'No se pudo optimizar la ruta';
          this.optimizing = false;
        },
      });
  }

  priorityClass(priority: DeliveryPriority): string {
    return `priority-${priority.toLowerCase()}`;
  }

  points(): Point[] {
    if (!this.result) {
      return [];
    }
    const raw: RawPoint[] = [
      { lat: this.result.warehouseLat, lng: this.result.warehouseLng, label: 'Deposito' },
      ...this.result.stops.map((stop) => ({
        lat: stop.latitude,
        lng: stop.longitude,
        label: String(stop.stopOrder),
        priority: stop.priority,
      })),
    ];
    const lats = raw.map((point) => point.lat);
    const lngs = raw.map((point) => point.lng);
    const minLat = Math.min(...lats);
    const maxLat = Math.max(...lats);
    const minLng = Math.min(...lngs);
    const maxLng = Math.max(...lngs);
    const latRange = maxLat - minLat || 0.01;
    const lngRange = maxLng - minLng || 0.01;

    return raw.map((point) => ({
      x: 40 + ((point.lng - minLng) / lngRange) * 520,
      y: 40 + ((maxLat - point.lat) / latRange) * 300,
      label: point.label,
      priority: point.priority,
    }));
  }

  segments(): Segment[] {
    const points = this.points();
    return points.slice(1).map((point, index) => ({
      x1: points[index].x,
      y1: points[index].y,
      x2: point.x,
      y2: point.y,
    }));
  }
}
