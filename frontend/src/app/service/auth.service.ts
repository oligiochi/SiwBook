import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private isAuthenticated: boolean = false;
  private username: string | null = null;
  private userId: number | null = null;
  private role: string | null = null;
  private loginUrl = 'http://localhost:8080/api/login';

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = { username, password };

    return this.http.post<any>(this.loginUrl, body, { headers })
      .pipe(
        map(response => {
          return response;
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('userId');
    localStorage.removeItem('username ');
    localStorage.removeItem('role ');
    this.isAuthenticated = false;
  }

  isAuthenticatedUser() : boolean {
    let token = null;
    if (typeof window !== "undefined")
      token = localStorage.getItem('token');
    return !!(token);
  }

  getUsername(): string | null {
    this.username = typeof window !== 'undefined' ? localStorage.getItem('username') : null
    return this.username;
  }

  getUserId(): number | null {
    if (typeof window !== 'undefined') {
      const userIdStr = localStorage.getItem('userId');
      this.userId = userIdStr ? +userIdStr : null;
    } else {
      this.userId = null;
    }
    return this.userId;
  }

  updateUsername(username : string | null): void {
    if (username) {
      localStorage.setItem('username', username);
    }
    else
      localStorage.removeItem('username');
  }

  getRole(): string | null {
    if (this.isAuthenticatedUser()) {
      this.role = typeof window !== 'undefined' ? localStorage.getItem('role') : null
    }
    return this.role;
  }
}
