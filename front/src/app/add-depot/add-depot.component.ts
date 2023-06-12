import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-depot',
  templateUrl: './add-depot.component.html',
  styleUrls: ['./add-depot.component.css']
})
export class AddDepotComponent implements OnInit {
  private baseUrl = 'http://localhost:8080';
  formData = {
    numero: '',
    name: '',
    adresse: '',
    
  };

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() { }

  onSubmit(form: NgForm) {
    //if (form.invalid) {
      //return;
    //}
  
    // Check if numeroDepot already exists
        // Form is valid and numeroDepot doesn't exist, send the form data to the server
        const url = `${this.baseUrl}/depots`;
        this.http.post(url, this.formData).subscribe(response => {
          this.snackBar.open('Ajout réussie', 'Close', { 
            duration: 3000,
            panelClass: ['success-snackbar'] // Add a custom class to the snackbar
          });
          form.resetForm();
        }, error => {
          this.snackBar.open('Ce numero de depot est dejà utilisé', 'Close', { 
            duration: 3000,
            panelClass: ['error-snackbar'] // Add a custom class to the snackbar
          });
        });
      }
}