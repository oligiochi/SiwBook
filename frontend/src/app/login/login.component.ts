// src/app/login/login.component.ts

import { Component }               from '@angular/core';
import { CommonModule }            from '@angular/common';
import { FormsModule }             from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule, Router }    from '@angular/router';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faGoogle, faGithub, faFacebook }  from '@fortawesome/free-brands-svg-icons';
import { faEye, faEyeSlash }              from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    FontAwesomeModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  // icone eye
  faEye      = faEye;
  faEyeSlash = faEyeSlash;

  // dati bindati
  username = '';
  password = '';
  showPassword = false;

  constructor(
    library: FaIconLibrary,
    private http: HttpClient,
    private router: Router
  ) {
    // registra anche le icone brands + eye
    library.addIcons(faGoogle, faGithub, faFacebook);
  }
  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  login() {
    this.http.post(
      'http://localhost:8080/api/login',
      { username: this.username, password: this.password },
      { withCredentials: true, observe: 'response' }
    ).subscribe({
      next: res => {
        if (res.status === 200) {
          this.router.navigate(['/']);
        }
      },
      error: () => alert('Login failed')
    });
  }

  oauthLogin(provider: string) {
    window.location.href =
      `http://localhost:8080/oauth2/authorization/${provider}`;
  }
}
