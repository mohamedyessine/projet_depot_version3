import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  username: string; // Define the username property
  password: string; // Define the password property
  errorMessage: string; // Define an error message property

  constructor(private authService: AuthService, private router: Router , private snackBar: MatSnackBar) { }

  ngOnInit() { }

  onSubmit(myForm: NgForm) {
    if (myForm.valid) {
      // Call the AuthService login method with the username and password
      this.authService.login(this.username, this.password)
        .subscribe(result => {
          if (result) {
            // Authentication successful, redirect to dashboard
            this.snackBar.open('Authentification réussie', 'Close', { 
              duration: 3000,
              panelClass: ['success-snackbar'] // Add a custom class to the snackbar
            });
            this.router.navigate(['/stock']);
          } else {
            // Authentication failed, show error message
            this.errorMessage = 'Nom utilisateur ou mot de passe invalide';
            this.snackBar.open('Nom utilisateur ou mot de passe invalide', 'Close', { 
              duration: 3000,
              panelClass: ['error-snackbar'] // Add a custom class to the snackbar
            });
          }
        }, error => {
          console.error('Erreur lors de la connexion:', error);
          this.errorMessage = "Une erreur s'est produite lors de la connexion";
          this.snackBar.open(error.error.message, 'Close', { 
            duration: 3000,
            panelClass: ['error-snackbar'] // Add a custom class to the snackbar
          });
        });
    }
  }

}