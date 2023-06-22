import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';
// import * as bcrypt from 'bcryptjs';

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
// Encrypt the password using bcrypt
// private encryptPassword(password: string): string {
//   const saltRounds = 10; // Number of salt rounds to use (recommended value: 10)
//   const salt = bcrypt.genSaltSync(saltRounds);
//   const encryptedPassword = bcrypt.hashSync(password, salt);
//   return encryptedPassword;
// }
signup(username: string, password: string, email: string): Observable<boolean> {
  return this.http.post('http://localhost:8080/api/auth/signup', { username, password, email })
    .pipe(
      map((response: any) => {
        const token = response.accessToken;
        if (token) {
          // localStorage.setItem('currentUser', JSON.stringify({ username, token }));
          // this.isAuthenticatedSubject.next(true);
          return true;
        } else {
          return false;
        }
      }),
      catchError((error) => {
        console.error('Error during signup:', error);
        return of(false);
      })
    );
}
deleteUserById(userId: number): Observable<void> {
  const url = `http://localhost:8080/api/auth/${userId}`;
  return this.http.delete<void>(url);
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