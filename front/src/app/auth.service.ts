import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
  ) { }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post('http://localhost:8080/api/auth/signin', { username, password })
      .pipe(
        map((response: any) => {
          // login successful if there's a jwt token in the response
          const token = response.accessToken;
          if (token) {
            // store token in local storage to keep user logged in between page refreshes
            localStorage.setItem('currentUser', JSON.stringify({ username, token }));
            this.isAuthenticatedSubject.next(true); // emit true to subscribers
            return true;
          } else {
            return false;
          }
        }),
        catchError((error) => {
          console.error('Error during login:', error);
          return of(false);
        })
      );
  }

  logout(): void {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    this.isAuthenticatedSubject.next(false); // emit false to subscribers
  }

  isAuthenticated(): boolean {
    // check if user is logged in by checking for token in local storage
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return !!currentUser?.token;
  }
}