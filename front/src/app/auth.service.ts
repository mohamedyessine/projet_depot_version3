import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';
// import * as bcrypt from 'bcryptjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private baseUrl = 'http://localhost:5000';

  constructor(private http: HttpClient) {
    this.checkAuthenticationStatus();
  }
   
  getHeaders(): HttpHeaders {
    // let token = localStorage.getItem('token');
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  private checkAuthenticationStatus(): void {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const isAuthenticated = !!currentUser?.token;
    this.isAuthenticatedSubject.next(isAuthenticated);
  }




  login(username: string, password: string): Observable<boolean> {
    const url = `${this.baseUrl}/api/auth/signin`;
    return this.http.post(url, { username, password })
      .pipe(
        map((response: any) => {
          // login successful if there's a jwt token in the response
          const token = response.accessToken;
          if (token) {
            // store token in local storage to keep user logged in between page refreshes
            localStorage.setItem('currentUser', JSON.stringify({ username, token }));
            // localStorage.setItem('token',  token );
            this.isAuthenticatedSubject.next(true); // emit true to subscribers
            return true;
          } else {
            return false;
          }
        }),
        catchError((error) => {
          //console.error('Error during login:', error);
          return of(false);
        })
      );
  }
 
  signup(username: string, password: string, email: string): Observable<boolean> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/api/auth/signup`;
    return this.http.post(url, { username, password, email, headers })
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
          //console.error('Error during signup:', error);
          return of(false);
        })
      );
  }
  deleteUserById(userId: number): Observable<void> {
    const headers = this.getHeaders();
    const url = `http://localhost:5000/api/auth/${userId}`;
    return this.http.delete<void>(url, {headers});
  }

  logout(): void {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    // localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false); // emit false to subscribers
  }

  isAuthenticated(): boolean {
    // check if user is logged in by checking for token in local storage
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return !!currentUser?.token;
  }

  IsloggedIn(){
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return !!currentUser?.token;
  }

  ExistUser(username: string, email: string): Observable<boolean> {
    const url = `${this.baseUrl}/api/auth/exists`;
    const params = new HttpParams()
      .set('username', username)
      .set('email', email);

    return this.http.get<boolean>(url, { params });
  }

  getUserDetails(token: string): Observable<any> {
    const url = `${this.baseUrl}/api/auth/user-details?token=${token}`;
    return this.http.get<any>(url);
  }
  
}