import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of } from 'rxjs';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
interface Depot {
  value: string;
  viewValue: string;
}
interface Bureau {
  value: string;
  viewValue: string;
  numero:string;
  
}

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {
  formData = {
    bureauId:'',
    code: '',
    quantity: ''
  };
  selectedValue: string;
  quantity: string = '';
  code: string;
  sourceBureau: string;
  targetBureau: string;
  sourceDepot: string;
  targetDepot: string;
  articles: any[] = [];
  data: any = {};
  filteredBureaux: any[] = [];
  selectedTargetDepotValue: string;
  bureaux: Bureau[] = [];
  bureaux1: Bureau[] = [];
  filteredArticles: any[] = [];
  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }
  

  getBureaux(): Observable<any> {
    return this.http.get('http://localhost:8080/bureau');
  }
  getStocks(): Observable<any> {
    return this.http.get('http://localhost:8080/stock');
  }

  selectedDepotValue: string;
  depots: Depot[] = [];
  depots1: Depot[] = [];

  getDepots(): Observable<any> {
    return this.http.get('http://localhost:8080/depots');
  }

  getAllBureauByDepot(depotId: string): Observable<Bureau[]> {
    return this.http.get<Bureau[]>('http://localhost:8080/bureau/' + depotId + '/bureau');
  }
 

  getBureauxByDepot(depotId: string): Observable<any[]> {
    if (depotId) {
      return this.getAllBureauByDepot(depotId).pipe(
        map((data: any[]) => {
          return data.map(item => {
            return {
              value: item.id,
              viewValue: item.name,
              numero: item.numero
            };
          });
        })
      );
    } else {
      return of([]); // Return an empty array if depotId is not provided
    }
  }
  
  
  getBureauxByDepotEmetteur() {
    this.getBureauxByDepot(this.selectedDepotValue).subscribe((bureaux: any[]) => {
      this.bureaux = bureaux;
      if (this.bureaux.length > 0) {
        this.sourceBureau = this.bureaux[0].value;
      } else {
        this.sourceBureau = null;
      }
      // Log the selected values
      console.log('Selected Depot Emetteur:', this.selectedDepotValue);
      console.log('Selected Bureau Emetteur:', this.sourceBureau);
    });
  }
  
  
 getBureauxByDepotRecepteur() {
  if (this.selectedTargetDepotValue !== this.selectedDepotValue) {
    this.getBureauxByDepot(this.selectedTargetDepotValue).subscribe((bureaux1: any[]) => {
      this.bureaux1 = bureaux1;
      if (this.bureaux1.length > 0) {
        this.targetBureau = this.bureaux1[0].value;
      } else {
        this.targetBureau = null;
      }
      // Log the selected values
      console.log('Selected Depot Recepteur:', this.selectedTargetDepotValue);
      console.log('Selected Bureau Recepteur:', this.targetBureau);
    });
  } else {
    // Reset the bureaux and targetBureau if the selected depot values are the same
    this.bureaux1 = [];
    this.targetBureau = null;
    // Log the selected values
    console.log('Selected Depot Recepteur:', this.selectedTargetDepotValue);
    console.log('Selected Bureau Recepteur:', this.targetBureau);
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

  getData() {
    this.http.get<any[]>('http://localhost:8080/articles').subscribe(
      (response) => {
        this.articles = response;
        this.filteredArticles = this.articles; 
      },
      (error) => {
        console.log(error);
      }
    );
  }

  onArticleSelection() {
    // Get the selected article
    const selectedArticle = this.filteredArticles.find(article => article.name === this.selectedValue);
    
   // Update the code field with the selected article's code
   this.data.code = selectedArticle ? selectedArticle.code : '';

   // If the selected article is not found in the filtered articles, set the selected value to null
   if (!selectedArticle) {
     this.selectedValue = null;
   }

     // Filter the depots for "Depot Recepteur" based on the selected "Depot Emetteur"
 // this.filteredBureaux = this.bureaux.filter(depot => depot.numero !== this.sourceBureau);
  }
  onCodeInput() {
    // Get the article with the inputted code
    const selectedArticle = this.filteredArticles.find(article => article.code === this.formData.code);
  
    // Update the selected article in the data object
    this.selectedValue = selectedArticle ? selectedArticle.name : '';
  }




  ngOnInit() {
    this.getData();
    this.getBureaux().subscribe((data: any[]) => {
      this.bureaux = data.filter(item => item.id !== 0).map(item => {
        return {
          value: item.id,
          viewValue: item.name,
          numero:item.numero
        };
      });
      
    });
    this.getDepots().subscribe((data: any[]) => {
      this.depots = data.map(item => {
        return {
          value: item.id,
          viewValue: item.name
        };
      });
    });
    this.getDepots().subscribe((data: any[]) => {
      this.depots1 = data.map(item => {
        return {
          value: item.id,
          viewValue: item.name
        };
      });
    });
    this.filteredArticles = this.articles; // Initialize the filtered articles with all articles
    this.selectedValue = ''; // Clear the selected value initially
   
  }
  onSubmit(form: NgForm) {
    this.http.get<any>('http://localhost:8080/articles/articles/' + this.data.code)
      .subscribe(response => {
        const articleId = response.id;
        const formData = {
          sourceBureauId: this.sourceBureau,
          articleId: articleId,
          targetBureauId: this.targetBureau,
          quantity: this.quantity,
          sourceDepotId: this.selectedDepotValue,
          targetDepotId: this.selectedTargetDepotValue
        };
  
        this.http.post('http://localhost:8080/transfers', formData)
          .subscribe(
            (response) => {
              console.log(response);
              this.snackBar.open(response['message'], 'Close', {
                duration: 3000,
                panelClass: ['success-snackbar']
              });
              form.resetForm();
            },
            (error) => {
              console.log(error);
              this.snackBar.open(error.error.message, 'Close', {
                duration: 3000,
                panelClass: ['error-snackbar']
              });
            }
          );
      },
      (error) => {
        console.log(error);
        this.snackBar.open(error.error.message, 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    );
  }
  
    
  }

// onSubmit(form: NgForm) {
//   // call the backend API to get the article ID
 
//         this.http.get<any>('http://localhost:8080/articles/articles/' + this.data.code)
//       .subscribe(response => {
//         // Get the ID from the response
//         const articleId = response.id;

//         // Get the values of sourceDepot and targetDepot
//       const sourceBureauId = this.sourceBureau;
//       const targetBureauId = this.targetBureau;
//       const sourceDepotId = this.selectedDepotValue;
//       const targetDepotId = this.selectedTargetDepotValue;
//        // Create a new form data object with the ID and depot values
//        const formData = {
//         sourceBureauId: sourceBureauId,
//         articleId: articleId,
//         targetBureauId: targetBureauId,
//         quantity: this.quantity,
//         sourceDepotId: sourceDepotId, // Include the selected sourceDepotId
//         targetDepotId: targetDepotId, 

//       };
  
//         // Create a new form data object with the ID instead of the code
//         // const formData = {
//         //   sourceDepotId: '1',
//         //   articleId: articleId,
//         //   quantity: this.quantity,
//         //   targetDepotId: this.selectedValue
//         // };
        
//         // call the backend API to transfer the article to the selected depot
//         this.http.post('http://localhost:8080/transfers', formData)
//           .subscribe(
//             (response) => {
//               console.log(response);
//               this.snackBar.open(response['message'], 'Close', { 
//                 duration: 3000,
//                 panelClass: ['success-snackbar'] // Add a custom class to the snackbar
//               });
//               form.resetForm();
//             },
//             (error) => {
//               console.log(error);
//               this.snackBar.open(error.error.message, 'Close', { 
//                 duration: 3000,
//                 panelClass: ['error-snackbar'] // Add a custom class to the snackbar
//               });
//             }
//           );
//       },
//       (error) => {
//         console.log(error);
//         this.snackBar.open(error.error.message, 'Close', { 
//           duration: 3000,
//           panelClass: ['error-snackbar'] // Add a custom class to the snackbar
//         });
//       }
//     );
// }
