import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';

 
interface Bureau {
  value: string;
  viewValue: string;
} 

declare var $: any;
@Component({
  selector: 'app-achat',
  templateUrl: './achat.component.html',
  styleUrls: ['./achat.component.css']
})
export class AchatComponent implements OnInit{
  private baseUrl = 'http://41.226.182.130:5000';
  data: any = {};
  formData = {
    bureauId:'',
    code: '',
    quantity: ''
  };
  articles: any[] = [];
  bureaux: Bureau[] = [];
  selectedDepotValue: string;
  selectedBureauValue: string;
  depots: Bureau[] = [];
  filteredArticles: any[] = [];
  searchInput: string = '';
  selectedValue: string =  '';
  constructor(private http: HttpClient, private snackBar: MatSnackBar, private elementRef: ElementRef) { }
  @ViewChild('selectElement', { static: true }) selectElement: ElementRef;

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }
  ngAfterViewInit() {
     this.getData();
     this.filteredArticles = this.articles;
     this.selectedValue = ''; 
  }
  onArticleSelect() {
   // console.log("Selected Article Code:")
    const selectedArticle = this.filteredArticles.find(article => article.name === this.selectedValue);
    if (selectedArticle) {
      this.formData.code = selectedArticle.code; // Set the code for the selected article
    //  console.log("Selected Article Code:", selectedArticle.code);
    }
  }
  
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
    

  }

  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles`;
    this.http.get<any[]>(url, {headers}).subscribe(
      (response) => {
        this.articles = response;
        this.filteredArticles = this.articles; 
      //  console.log("hhh "+ this.filteredArticles);
        
        // $('.selectpicker').selectpicker();
        this.populateSelect(this.filteredArticles);
        this.initializeSelectPicker();
      },
      (error) => {
        this.snackBar.open("Aucun article exist.", 'Close', { 
          duration: 3000,
          panelClass: ['error-snackbar'] // Add a custom class to the snackbar
        });
      }
    );
  }
  populateSelect(options: any[]):void{
    //console.log("fdfddfd");
    
    const select = this.selectElement.nativeElement;
    // console.log("khijhfdfhd " +select);
    // console.log("yaaaa " +options);

    
    options.forEach(option => {
      const optionElement = document.createElement('option');
      optionElement.value = option.id;
      optionElement.text = option.name;
      
      
      select.appendChild(optionElement);
    });

    select.addEventListener('change', () => {
      const selectedOption = options.find(option => option.id.toString() === select.value.toString());
  
    //  console.log("selectedOption: ", selectedOption);
  
      if (selectedOption) {
        this.formData.code = selectedOption.code;
      } else {
        this.formData.code = '';
      }
    });
  }
  initializeSelectPicker():void{
    // $(this.selectElement.nativeElement).selectpicker();
    $(this.selectElement.nativeElement).selectpicker('refresh');
  }
  getBureau(): Observable<any> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/bureau`;
    return this.http.get(url, {headers});
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
    const headers = this.getHeaders();
    this.http.get<any>(url + this.formData.code, {headers})
      .subscribe(response => {
        // Get the ID from the response
        const articleId = response.id;
        const bureauId = this.bureaux.find(depot => depot.value === this.selectedValue);
        // Create a new form data object with the ID instead of the code
        const formDataWithId = {
          bureauId: bureauId.value,
          // depotId:this.selectedDepotValue,
          // bureauId:1,
          // depotId:1,
          articleId: articleId,
          quantity: this.formData.quantity
        };
  
        // Make an HTTP request to send the form data to the backend with the ID
        const url1 = `${this.baseUrl}/articles/add-in-bureau`;
        this.http.post(url1, formDataWithId, {headers})
          .subscribe(response => {
            //console.log('Form submitted:', response);
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
