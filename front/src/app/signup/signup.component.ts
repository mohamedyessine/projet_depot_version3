import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'app/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  username: string; // Define the username property
  email: string; // Define the email property
  password: string; // Define the password property
  errorMessage: string; // Define an error message property

  constructor(private authService: AuthService, private router: Router , private snackBar: MatSnackBar) { }

  ngOnInit() { }

  // onSubmit(myForm: NgForm) {
  //   if (myForm.valid) {
  //     // Call the AuthService login method with the username and password
  //     this.authService.signup(this.username, this.password, this.email)
  //       .subscribe(result => {
  //         if (result) {
  //           // Authentication successful, redirect to dashboard
  //           this.snackBar.open('Authentification réussie', 'Close', { 
  //             duration: 3000,
  //             panelClass: ['success-snackbar'] // Add a custom class to the snackbar
  //           });
  //           this.router.navigate(['/liste-users']);
  //         } else {
  //           // Authentication failed, show error message
  //           this.errorMessage = 'Invalid username or password';
  //           this.snackBar.open('Nom utilisateur ou mot de passe invalide', 'Close', { 
  //             duration: 3000,
  //             panelClass: ['error-snackbar'] // Add a custom class to the snackbar
  //           });
  //         }
  //       }, error => {
  //         console.error('Error during connexion:', error);
  //         this.errorMessage = 'An error occurred during connexion';
  //         this.snackBar.open(error.error.message, 'Close', { 
  //           duration: 3000,
  //           panelClass: ['error-snackbar'] // Add a custom class to the snackbar
  //         });
  //       });
  //   }
  // }
  onSubmit(myForm: NgForm) {
    if (myForm.valid) {
      this.authService.signup(this.username, this.password, this.email)
        .subscribe(result => {
          if (result) {
            // User signup successful, show success message or perform desired actions
            this.snackBar.open("Utilisateur ajouté avec succès.", 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar'] // Add a custom class to the snackbar
            });
            // Clear the form fields if needed
            myForm.reset();
            this.router.navigate(['/liste-users']);
          } else {
            // User signup failed, show error message
            this.errorMessage = "Échec de l'ajout de l'utilisateur.";
            this.snackBar.open("Échec de l'ajout de l'utilisateur.", 'Close', {
              duration: 3000,
              panelClass: ['error-snackbar'] // Add a custom class to the snackbar
            });
          }
        }, error => {
          console.error("Une erreur s'est produite lors de l'inscription.", error);
          this.errorMessage = "Une erreur s'est produite lors de l'inscription.";
          this.snackBar.open(error.error.message, 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar'] // Add a custom class to the snackbar
          });
        });
    }
  }
  
}
