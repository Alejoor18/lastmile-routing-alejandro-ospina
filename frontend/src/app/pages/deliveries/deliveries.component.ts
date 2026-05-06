import { DecimalPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Delivery, DeliveryPriority, DeliveryRequest, DeliveryStatus } from '../../models/api.models';
import { DeliveryService } from '../../services/delivery.service';

@Component({
  selector: 'app-deliveries',
  standalone: true,
  imports: [DecimalPipe, FormsModule, NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './deliveries.component.html',
})
export class DeliveriesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly deliveryService = inject(DeliveryService);

  protected readonly statuses = Object.values(DeliveryStatus);
  protected readonly priorities = Object.values(DeliveryPriority);
  protected deliveries: Delivery[] = [];
  protected statusFilter: DeliveryStatus | '' = '';
  protected priorityFilter: DeliveryPriority | '' = '';
  protected loading = false;
  protected saving = false;
  protected editingId: number | null = null;
  protected message = '';
  protected error = '';

  protected readonly form = this.fb.nonNullable.group({
    recipientName: ['', [Validators.required]],
    address: ['', [Validators.required]],
    latitude: [6.2442, [Validators.required]],
    longitude: [-75.5812, [Validators.required]],
    packageWeightKg: [1, [Validators.required, Validators.min(0.1)]],
    priority: [DeliveryPriority.MEDIUM, [Validators.required]],
    status: [DeliveryStatus.PENDING, [Validators.required]],
    notes: [''],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.deliveryService.list({ status: this.statusFilter, priority: this.priorityFilter }).subscribe({
      next: (deliveries) => {
        this.deliveries = deliveries;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message ?? 'No se pudieron cargar las entregas';
        this.loading = false;
      },
    });
  }

  submit(): void {
    this.message = '';
    this.error = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    const request = this.payload();
    const save$ = this.editingId
      ? this.deliveryService.update(this.editingId, request)
      : this.deliveryService.create(request);

    save$.subscribe({
      next: () => {
        this.message = this.editingId ? 'Entrega actualizada' : 'Entrega creada';
        this.resetForm();
        this.saving = false;
        this.load();
      },
      error: (error) => {
        this.error = error.error?.message ?? 'No se pudo guardar la entrega';
        this.saving = false;
      },
    });
  }

  edit(delivery: Delivery): void {
    this.editingId = delivery.id;
    this.form.patchValue({
      recipientName: delivery.recipientName,
      address: delivery.address,
      latitude: delivery.latitude,
      longitude: delivery.longitude,
      packageWeightKg: delivery.packageWeightKg,
      priority: delivery.priority,
      status: delivery.status,
      notes: delivery.notes ?? '',
    });
  }

  updateStatus(delivery: Delivery, status: DeliveryStatus): void {
    this.deliveryService.updateStatus(delivery.id, status).subscribe({
      next: () => {
        this.message = 'Estado actualizado';
        this.load();
      },
      error: (error) => (this.error = error.error?.message ?? 'No se pudo actualizar el estado'),
    });
  }

  remove(delivery: Delivery): void {
    this.deliveryService.delete(delivery.id).subscribe({
      next: () => {
        this.message = 'Entrega eliminada';
        this.load();
      },
      error: (error) => (this.error = error.error?.message ?? 'No se pudo eliminar la entrega'),
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form.reset({
      recipientName: '',
      address: '',
      latitude: 6.2442,
      longitude: -75.5812,
      packageWeightKg: 1,
      priority: DeliveryPriority.MEDIUM,
      status: DeliveryStatus.PENDING,
      notes: '',
    });
  }

  private payload(): DeliveryRequest {
    const value = this.form.getRawValue();
    return {
      recipientName: value.recipientName,
      address: value.address,
      latitude: Number(value.latitude),
      longitude: Number(value.longitude),
      packageWeightKg: Number(value.packageWeightKg),
      priority: value.priority,
      status: value.status,
      notes: value.notes || null,
    };
  }
}
