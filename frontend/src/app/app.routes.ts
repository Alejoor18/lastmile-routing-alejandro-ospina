import { Routes } from '@angular/router';
import { authGuard, entryGuard, guestGuard } from './auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DeliveriesComponent } from './pages/deliveries/deliveries.component';
import { LoginComponent } from './pages/login/login.component';
import { OptimizerComponent } from './pages/optimizer/optimizer.component';
import { RedirectComponent } from './pages/redirect.component';
import { RegisterComponent } from './pages/register/register.component';
import { RoutesComponent } from './pages/routes/routes.component';

export const routes: Routes = [
  { path: '', component: RedirectComponent, canActivate: [entryGuard] },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'deliveries', component: DeliveriesComponent },
      { path: 'optimizer', component: OptimizerComponent },
      { path: 'routes', component: RoutesComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];
