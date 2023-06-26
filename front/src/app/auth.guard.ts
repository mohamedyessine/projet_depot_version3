import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { AuthService } from './auth.service';
import { catchError, map, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  // canActivate(): Observable<boolean> {
  //   return this.authService.isAuthenticated$.pipe(
  //     map((isAuthenticated: boolean) => {
  //       if (isAuthenticated) {
  //         return true; // Allow access to the route
  //       } else {
  //         this.router.navigate(['/login']); // Redirect to the login page
  //         return false; // Deny access to the route
  //       }
  //     }),
  //     catchError((error) => {
  //       console.error('Error during authentication:', error);
  //       this.router.navigate(['/login']); // Redirect to the login page
  //       return of(false); // Deny access to the route
  //     })
  //   );
  // }
  // canActivate(): Observable<boolean> {
  //   // return this.authService.isAuthenticated$.pipe(
  //   //   tap((isAuthenticated: boolean) => {
  //   //     console.log('isAuthenticated:', isAuthenticated);
  //   //   }),
  //   //   map((isAuthenticated: boolean) => {
  //   //     if (isAuthenticated) {
  //   //       return true; // Allow access to the route
  //   //     } else {
  //   //       this.router.navigate(['/login']); // Redirect to the login page
  //   //       return false; // Deny access to the route
  //   //     }
  //   //   })
  //   // );.

    

  // }
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.IsloggedIn()) {
      const currentUser = JSON.parse(localStorage.getItem('currentUser'));
      return this.authService.getUserDetails(currentUser?.token).pipe(
        switchMap((userDetails: any) => {

          if (!userDetails) {
            const errorMessage = 'Error retrieving user details';
            alert(errorMessage); // Display error message
            this.router.navigate(['/login']); // Redirect to login page
            return of(false);
          }

          const username = userDetails.username;
          const email = userDetails.email;
  
          return this.authService.ExistUser(username, email).pipe(
            tap((exists: boolean) => {
              if (!exists) {
                const message = `User with username ${username} and email ${email} does not exist`;
                alert(message); // Display alert for non-existing user
                this.router.navigate(['/login']); // Redirect to login page
              }
            }),
            map((exists: boolean) => exists),
            catchError(() => {
              const errorMessage = 'Error retrieving user details';
              alert(errorMessage); // Display error message
              this.router.navigate(['/login']); // Redirect to login page
              return of(false);
            })
          );
        }),
        catchError(() => {
          const errorMessage = 'Error retrieving user details';
          alert(errorMessage); // Display error message
          this.router.navigate(['/login']); // Redirect to login page
          return of(false);
        })
      );
    } else {
      this.router.navigate(['/login']); // Redirect to login page
      return false;
    }
  }
  
 
}
