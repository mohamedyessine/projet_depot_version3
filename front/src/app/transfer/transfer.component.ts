import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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
declare var $: any;

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {
  private baseUrl = 'http://localhost:8080';
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
  constructor(private http: HttpClient, private snackBar: MatSnackBar, private elementRef: ElementRef) { }
  @ViewChild('selectElement', { static: true }) selectElement: ElementRef;

  ngAfterViewInit() {
     this.getData();
     this.filteredArticles = this.articles;
     this.selectedValue = ''; 
  }

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  } 
  getBureaux(): Observable<any> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/bureau`;
    return this.http.get(url, {headers});
  }
  getStocks(): Observable<any> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/stock`;
    return this.http.get(url, {headers});
  }

  selectedDepotValue: string;
  depots: Depot[] = [];
  depots1: Depot[] = [];

  getDepots(): Observable<any> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/depots`;
    return this.http.get(url, {headers});
  }

  getAllBureauByDepot(depotId: string): Observable<Bureau[]> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/bureau/`;
    return this.http.get<Bureau[]>(url + depotId + '/bureau', {headers});
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
      // if (this.bureaux.length > 0) {
      //   this.sourceBureau = this.bureaux[0].value;
      // } else {
      //   this.sourceBureau = null;
      // }
      // Log the selected values
      console.log('Selected Depot Emetteur:', this.selectedDepotValue);
      console.log('Selected Bureau Emetteur:', this.sourceBureau);
    });
  }
  
  
 getBureauxByDepotRecepteur() {
  // if (this.selectedTargetDepotValue !== this.selectedDepotValue) {

  if (this.selectedTargetDepotValue !== this.selectedDepotValue) {
    this.getBureauxByDepot(this.selectedTargetDepotValue).subscribe((bureaux1: any[]) => {
      this.bureaux1 = bureaux1;
      // Log the selected values
      console.log('Selected Depot Recepteur:', this.selectedTargetDepotValue);
      console.log('Selected Bureau Recepteur:', this.targetBureau);
    });
  } else {
    this.getBureauxByDepot(this.selectedTargetDepotValue).subscribe((bureaux1: any[]) => {
      this.bureaux1 = bureaux1;
      const index = this.bureaux1.findIndex(bureau => bureau.value === this.sourceBureau);
      if (index !== -1) {
        this.bureaux1.splice(index, 1); // Remove the selected bureau emetteur from the options
      }
      // Check if the targetBureau is the same as the sourceBureau and reset it if necessary
      if (this.targetBureau === this.sourceBureau) {
        this.targetBureau = null;
      }
      // if (this.bureaux1.length > 0) {
      //   this.targetBureau = this.bureaux1[0].value;
      // } else {
      //   this.targetBureau = null;
      // }
      // Log the selected values
      console.log('Selected Depot Recepteur:', this.selectedTargetDepotValue);
      console.log('Selected Bureau Recepteur:', this.targetBureau);
    });
  } 
  // else {
  //   // Reset the bureaux and targetBureau if the selected depot values are the same
  //   this.bureaux1 = [];
  //   this.targetBureau = null;
  //   // Log the selected values
  //   console.log('Selected Depot Recepteur:', this.selectedTargetDepotValue);
  //   console.log('Selected Bureau Recepteur:', this.targetBureau);
  // }
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
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles`;
    this.http.get<any[]>(url, {headers}).subscribe(
      (response) => {
        this.articles = response;
        this.filteredArticles = this.articles; 
        console.log("hhh "+ this.filteredArticles);
        
        // $('.selectpicker').selectpicker();
        this.populateSelect(this.filteredArticles);
        this.initializeSelectPicker();
      },
      (error) => {
        console.log("error");
      }
    );
  }
  populateSelect(options: any[]):void{
    console.log("fdfddfd");
    
    const select = this.selectElement.nativeElement;
    console.log("khijhfdfhd " +select);
    console.log("yaaaa " +options);

    
    options.forEach(option => {
      const optionElement = document.createElement('option');
      optionElement.value = option.id;
      optionElement.text = option.name;
      
      
      select.appendChild(optionElement);
    });

    select.addEventListener('change', () => {
      const selectedOption = options.find(option => option.id.toString() === select.value.toString());
  
      console.log("selectedOption: ", selectedOption);
  
      if (selectedOption) {
        this.data.code = selectedOption.code;
      } else {
        this.data.code = '';
      }
    });
  }
  initializeSelectPicker():void{
    // $(this.selectElement.nativeElement).selectpicker();
    $(this.selectElement.nativeElement).selectpicker('refresh');
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
    // this.getData();
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
    // this.filteredArticles = this.articles; // Initialize the filtered articles with all articles
    // this.selectedValue = ''; // Clear the selected value initially
   
  }
  onSubmit(form: NgForm) {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles/articles/`;
    this.http.get<any>(url + this.data.code, {headers})
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
        const url1 = `${this.baseUrl}/transfers`;
        this.http.post(url1, formData, {headers})
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
