import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import {HomeComponent} from './home/home.component';
//import { AuthGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent},
  {path:'',component:HomeComponent},
  { path: '**', redirectTo: '', pathMatch: 'full' }
];
