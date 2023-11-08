import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
    providedIn: 'root',
  })
export class ArticlesService {
    private apiUrl = 'http://localhost:5000/articles';
constructor(private http: HttpClient) { }

getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  getByType(articleType: string): Observable<any> {
    const baseHeaders = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    const securedHeaders = this.getHeaders();
    const headers = baseHeaders.append('Authorization', securedHeaders.get('Authorization'));
    return this.http
      .post(`${this.apiUrl}/type`, { type: articleType }, { headers: headers })
      .pipe(
        catchError((error: any) => {
          return throwError(error);
        })
      );
  }

  searchArticles(searchTerm: string): Observable<any[]> {
    const headers = this.getHeaders();
    const url = `${this.apiUrl}/search?searchTerm=${searchTerm}`;
    return this.http.get<any[]>(url, {headers});
  }

}
