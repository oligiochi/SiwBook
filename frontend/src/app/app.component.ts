import {Component, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {RouterOutlet, RouterLink, RouterLinkActive} from '@angular/router';
import {NgForOf, NgIf} from "@angular/common";
import { faUser } from '@fortawesome/free-solid-svg-icons';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import { AuthService } from "./service/auth.service";

@Component({
  selector: 'app-root',
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    NgForOf,
    NgIf,
    NgOptimizedImage,
    FaIconComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // <- CORRETTO
})

export class AppComponent {
  isLoggedIn = false;
  title = 'SiwBook';
  profileImageUrl: string | null = 'assets/profile.jpg';
  faUser = faUser;
  menuOpen = false;




}
