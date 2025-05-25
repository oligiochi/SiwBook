import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
//import { AuthGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { path: '**', redirectTo: '', pathMatch: 'full' }
];
