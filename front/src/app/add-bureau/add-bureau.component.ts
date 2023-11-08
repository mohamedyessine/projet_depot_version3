import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-bureau',
  templateUrl: './add-bureau.component.html',
  styleUrls: ['./add-bureau.component.css']
})
export class AddBureauComponent implements OnInit {
  private baseUrl = 'http://localhost:5000';
  data: any = {};
  formData = {
    depotId: '',
    numero: '',
    name: ''
  };
  depots: any[] = [];

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() { this.getData(); }

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/depots`;
    this.http.get<any[]>(url, { headers }).subscribe(
      (response) => {
        this.depots = response;
      },
      (error) => {
        console.log(error);
      }
    );
  }

  onDepotsSelection() {
    // Get the selected article
    const selectedDepot = this.depots.find(depot => depot.name === this.data.depot);

    // Update the code field with the selected article's code
    this.data.numero = selectedDepot.numero;
  }

  onSubmit(form: NgForm) {
    // Check if the form is valid
    // if (form.invalid) {
    //   return;
    // }
  
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/depots/numero/`;
    // Make an HTTP request to get the ID of the depot
    this.http.get<any>(url + this.data.numero, { headers })
      .subscribe(response => {
        // Get the ID from the response
        const depotId = response.id;
  
        // Create a new bureau object
        const bureauData = {
          numero: this.formData.numero,
          name: this.formData.name
        };
        const url1 = `${this.baseUrl}/bureau/create?depotId=`;
        // Make an HTTP request to create the bureau in the depot
        this.http.post(url1 + depotId, bureauData, { headers })
          .subscribe(response => {
            console.log('Bureau created:', response);
            this.snackBar.open('Bureau créé avec succès.', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar'] // Add a custom class to the snackbar
            });
          }, error => {
            this.snackBar.open('le code existe déjà', 'Close', {
              duration: 3000,
              panelClass: ['error-snackbar'] // Add a custom class to the snackbar
            });
          })
          .add(() => {
            form.resetForm(); // Reset the form after the HTTP request completes
          });
      });
  }
  

}
