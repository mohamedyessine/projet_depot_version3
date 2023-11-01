import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-depot',
  templateUrl: './add-depot.component.html',
  styleUrls: ['./add-depot.component.css']
})
export class AddDepotComponent implements OnInit {
  private baseUrl = 'http://41.226.182.130:5000';
  formData = {
    numero: '',
    name: '',
    adresse: '',
    
  };

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() { }

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  onSubmit(form: NgForm) {
    //if (form.invalid) {
      //return;
    //}
  
    // Check if numeroDepot already exists
        // Form is valid and numeroDepot doesn't exist, send the form data to the server
        const headers = this.getHeaders();
        const url = `${this.baseUrl}/depots`;
        this.http.post(url, this.formData, {headers}).subscribe(response => {
          this.snackBar.open('Dépôt créé avec succès.', 'Close', { 
            duration: 3000,
            panelClass: ['success-snackbar'] // Add a custom class to the snackbar
          });
          form.resetForm();
        }, error => {
          this.snackBar.open('Le Dépôt est déja exist', 'Close', { 
            duration: 3000,
            panelClass: ['error-snackbar'] // Add a custom class to the snackbar
          });
        });
      }
}
