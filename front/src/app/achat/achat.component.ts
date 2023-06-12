import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { log } from 'console';
interface Bureau {
  value: string;
  viewValue: string;
}
@Component({
  selector: 'app-achat',
  templateUrl: './achat.component.html',
  styleUrls: ['./achat.component.css']
})
export class AchatComponent implements OnInit {
  private baseUrl = 'http://localhost:8080';
  data: any = {};
  formData = {
    bureauId:'',
    code: '',
    quantity: ''
  };
  articles: any[] = [];
  bureaux: Bureau[] = [];
  selectedValue: string;
  selectedDepotValue: string;
  depots: Bureau[] = [];
  filteredArticles: any[] = [];
  searchInput: string = '';

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  getDepots(): Observable<any> {
    const url = `${this.baseUrl}/depots`;
    return this.http.get(url);
  }
  getAllBureauByDepot(depotId: string): Observable<Bureau[]> {
    const url = `${this.baseUrl}/bureau/`;
    return this.http.get<Bureau[]>(url + depotId + '/bureau');
  }
 
  getBureauxByDepot() {
    if (this.selectedDepotValue) {
      this.getAllBureauByDepot(this.selectedDepotValue).subscribe((data: any[]) => {
        this.bureaux = data.map(item => {
          return {
            value: item.id,
          viewValue: item.name
          };
        });
      });
    }
  }

 
 
  filterItem(event: string) {
    
    if (!event) {
      this.filteredArticles = this.articles;
    } else {
      this.filteredArticles = this.articles.filter(article =>
        article.name.toLowerCase().startsWith(event.toLowerCase())
      );
    }
  }
  onChangeofOptions(event: any) {
    // Do something with the selected option
    console.log(event);
  }
  ngOnInit() { 
    this.getData();
    this.getDepots().subscribe((data: any[]) => {
      this.depots = data.map(item => {
        return {
          value: item.id,
          viewValue: item.name
        };
      });
    });
    this.getBureau().subscribe((data: any[]) => {
      this.bureaux = data.map(item => {
        return {
          value: item.id,
          viewValue: item.name
        };
      });
    });
    this.filteredArticles = this.articles; // Initialize the filtered articles with all articles
    this.selectedValue = ''; // Clear the selected value initially
  
  }

  getData() {
    const url = `${this.baseUrl}/articles`;
    this.http.get<any[]>(url).subscribe(
      (response) => {
        this.articles = response;
        this.filteredArticles = this.articles; 
      },
      (error) => {
        console.log(error);
      }
    );
  }
  getBureau(): Observable<any> {
    const url = `${this.baseUrl}/bureau`;
    return this.http.get(url);
  }

  // onArticleSelection() {
  //   // Get the selected article
  //   const selectedArticle = this.articles.find(article => article.name === this.data.article);
    
  //   // Update the code field with the selected article's code
  //   this.formData.code = selectedArticle.code;
  // }
  onArticleSelection() {
  // Get the selected article
  const selectedArticle = this.filteredArticles.find(article => article.name === this.selectedValue);

  // Update the code field with the selected article's code
  this.formData.code = selectedArticle ? selectedArticle.code : '';

  // If the selected article is not found in the filtered articles, set the selected value to null
  if (!selectedArticle) {
    this.selectedValue = null;
  }
}

onCodeInput() {
  // Get the article with the inputted code
  const selectedArticle = this.filteredArticles.find(article => article.code === this.formData.code);

  // Update the selected article in the data object
  this.selectedValue = selectedArticle ? selectedArticle.name : '';
}

  
  
  

  

  onSubmit(form: NgForm) {
    // Check if the form is valid
    if (form.invalid) {
      return;
    }

   
  
    // Make an HTTP request to get the ID of the article
    const url = `${this.baseUrl}/articles/articles/`;
    this.http.get<any>(url + this.formData.code)
      .subscribe(response => {
        // Get the ID from the response
        const articleId = response.id;
  
        // Create a new form data object with the ID instead of the code
        const formDataWithId = {
          // bureauId: this.formData.bureauId,
          // depotId:this.selectedDepotValue,
          bureauId:2,
          depotId:2,
          articleId: articleId,
          quantity: this.formData.quantity
        };
  
        // Make an HTTP request to send the form data to the backend with the ID
        const url1 = `${this.baseUrl}/articles/add`;
        this.http.post(url1, formDataWithId)
          .subscribe(response => {
            console.log('Form submitted:', response);
            this.snackBar.open(response['message'], 'Close', { 
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
