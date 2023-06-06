import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-article',
  templateUrl: './add-article.component.html',
  styleUrls: ['./add-article.component.css']
})
export class AddArticleComponent implements OnInit {

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() {
  }

  formData = {
    code: '',
    name: '',
    lebelle: ''
  };

  onSubmit(form: NgForm) {
    //if (form.invalid) {
      //return;
    //}
  
    // Check if numeroDepot already exists
        // Form is valid and numeroDepot doesn't exist, send the form data to the server
        this.http.post('http://localhost:8080/articles', this.formData).subscribe(response => {
          this.snackBar.open("Added successfully", 'Close', { 
            duration: 3000,
            panelClass: ['success-snackbar'] // Add a custom class to the snackbar
          });
          form.resetForm();
        }, error => {
          this.snackBar.open(error.error.message, 'Close', { 
            duration: 3000,
            panelClass: ['error-snackbar'] // Add a custom class to the snackbar
          });
        });
      }

}
