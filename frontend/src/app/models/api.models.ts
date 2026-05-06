export enum UserRole {
  ADMIN = 'ADMIN',
  USER = 'USER',
}

export enum DeliveryStatus {
  PENDING = 'PENDING',
  ASSIGNED = 'ASSIGNED',
  IN_TRANSIT = 'IN_TRANSIT',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export enum DeliveryPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
}

export enum RouteStatus {
  PLANNED = 'PLANNED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  createdAt?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  fullName: string;
  email: string;
  role: UserRole;
}

export interface Driver {
  id: number;
  name: string;
  phone: string;
  vehiclePlate: string;
  vehicleType: string;
  capacityKg: number;
  active: boolean;
}

export interface DriverRequest {
  name: string;
  phone: string;
  vehiclePlate: string;
  vehicleType: string;
  capacityKg: number;
  active: boolean;
}

export interface Delivery {
  id: number;
  recipientName: string;
  address: string;
  latitude: number;
  longitude: number;
  packageWeightKg: number;
  priority: DeliveryPriority;
  status: DeliveryStatus;
  timeWindowStart?: string | null;
  timeWindowEnd?: string | null;
  notes?: string | null;
  createdAt: string;
}

export interface DeliveryRequest {
  recipientName: string;
  address: string;
  latitude: number;
  longitude: number;
  packageWeightKg: number;
  priority: DeliveryPriority;
  status: DeliveryStatus;
  notes?: string | null;
}

export interface RouteOptimizeRequest {
  driverId: number;
  warehouseLat: number;
  warehouseLng: number;
  deliveryIds?: number[];
}

export interface RouteStop {
  id: number;
  deliveryId: number;
  recipientName: string;
  address: string;
  latitude: number;
  longitude: number;
  packageWeightKg: number;
  priority: DeliveryPriority;
  status: DeliveryStatus;
  stopOrder: number;
  distanceFromPreviousKm: number;
  estimatedArrival: string;
  notes?: string | null;
}

export interface RoutePlan {
  id: number;
  name: string;
  driver: Driver;
  createdAt: string;
  warehouseLat: number;
  warehouseLng: number;
  totalDistanceKm: number;
  estimatedDurationMinutes: number;
  status: RouteStatus;
  stops: RouteStop[];
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface BackendHealth {
  status: string;
  service: string;
}
