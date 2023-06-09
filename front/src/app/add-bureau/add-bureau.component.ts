import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-bureau',
  templateUrl: './add-bureau.component.html',
  styleUrls: ['./add-bureau.component.css']
})
export class AddBureauComponent implements OnInit {
  
  data: any = {};
  formData = {
    depotId:'',
    numero: '',
    name: ''
  };
  depots: any[] = [];

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() { this.getData();}

  getData() {
    this.http.get<any[]>('http://localhost:8080/depots').subscribe(
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
    this.data.numero   = selectedDepot.numero;
  }

  onSubmit(form: NgForm) {
    // Check if the form is valid
    if (form.invalid) {
      return;
    }

   
  
   // Make an HTTP request to get the ID of the depot
  this.http.get<any>('http://localhost:8080/depots/numero/' + this.data.numero)
  .subscribe(response => {
    // Get the ID from the response
    const depotId = response.id;

    // Create a new bureau object
    const bureauData = {
      numero: this.formData.numero,
      name: this.formData.name
    };

    // Make an HTTP request to create the bureau in the depot
    this.http.post('http://localhost:8080/bureau/create?depotId=' + depotId, bureauData)
      .subscribe(response => {
        console.log('Bureau created:', response);
        this.snackBar.open('Bureau created successfully.', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar'] // Add a custom class to the snackbar
        });
        form.resetForm();
      },
      (error) => {
        console.log(error);
        this.snackBar.open(error.error.message, 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar'] // Add a custom class to the snackbar
        });
      });
  },
  (error) => {
    console.log(error);
    this.snackBar.open(error.error.message, 'Close', {
      duration: 3000,
      panelClass: ['error-snackbar'] // Add a custom class to the snackbar
    });
  });
  }
  
}
