import { Component, ElementRef, OnInit, Output, Renderer2, ViewChild , EventEmitter} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
// import * as XLSX from 'xlsx';


interface CreateDefectueuxRequest {
  article: number;
  sourceBureau: number;
  quantity: number;
}
 interface Article {
  id: number;
  name: string;
}

interface Defectieux {
  id: number;
  article: {
    id: number;
    name: string;
  };
  bureau: {
    id: number;
    name: string;
  };
  quantity: number;
}



interface Bureau { 
  value: string;
  viewValue: string;

}

interface TableData {
  bureau: {
    id: number;
    name: string;
  };
  article: {
    id: number;
    name: string;
  };
  quantity: number;
}

interface TableFullData {
  bureau: {
    name: string;
  };
  articleWithQuantity: {
    article: {
      name: string;
    }
    quantity: number;

  };

}


@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.css']
})
export class StockComponent implements OnInit {
  @Output() closeModalEvent = new EventEmitter<void>();
  @ViewChild('myModalRef', { static: false }) myModalRef: ElementRef;



  private baseUrl = 'http://localhost:8080';
  private apiUrl = 'http://localhost:8080/defectueux';
  tableData: TableData[] = [];
  tableFullData: TableFullData[] = [];
  selectedValue: string;
  newQuantity: number;
  selectedItem: any;
  tableData1!: TableData;
  // Define the page and page size variables
  page = 1;
  pageSize = 10;
  item: TableData;
  constructor(private http: HttpClient, private renderer: Renderer2, private snackBar: MatSnackBar) { }
  bureaux: Bureau[] = [];
  defectueuxCreated = false;
  action: string;

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  setSelectedItem(item: any) {
    this.selectedItem = item;
  }
  setAction(action: string) {
    this.action = action;
  }
 
  getBureau(): Observable<any> {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/bureau`;
    return this.http.get(url, {headers});
  }

  selectedDepotValue: string;
  depots: Bureau[] = [];

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
  closeModal() {
    this.myModalRef.nativeElement.classList.remove('show');
    this.myModalRef.nativeElement.style.display = 'none';
    const modalBackdrop = document.getElementsByClassName('modal-backdrop')[0];
    modalBackdrop.parentNode.removeChild(modalBackdrop);
    this.newQuantity=null;
  }
  
  createDefectueux(articleId: number, sourceBureauId: number, quantity: number) {
    const selectedArticleId = this.selectedItem.article.id;
    const selectedBureauId = this.selectedItem.bureau.id;
    const headers = this.getHeaders();
    const request = {
      articleId: selectedArticleId,
      sourceBureauId: selectedBureauId,
      quantity: quantity
    };
    const url = `${this.apiUrl}/add?articleId=${articleId}&sourceBureauId=${sourceBureauId}&quantity=${quantity}`;
    this.http.post<Defectieux>(url, {headers}).subscribe(
      (defectueux: Defectieux) => {
        this.defectueuxCreated = true;
        setTimeout(() => {
          this.defectueuxCreated = false; // Reset the flag after a certain time (e.g., 3 seconds)
          this.closeModal();
          // this.closeModalEvent.emit();
        }, 300);
        this.snackBar.open('Defectueux added successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar'] // Add a custom class to the snackbar
        });
        console.log('Defectueux created:', defectueux);
        // Handle any necessary actions upon successful creation
        this.getStock();
       
      },
      (error: any) => {
        this.snackBar.open(error.error.message, 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar'] // Add a custom class to the snackbar
        });
        console.error('Error creating defectueux:', error);
        // Handle error if the creation fails
      }
      
    );
    
  }
  
  
  reparerArticle(articleId: number, sourceBureauId: number, quantity: number) {
    const selectedArticleId = this.selectedItem.article.id;
    const selectedBureauId = this.selectedItem.bureau.id;
    const headers = this.getHeaders();
    const request = {
      articleId: selectedArticleId,
      sourceBureauId: selectedBureauId,
      quantity: quantity
    };
    const url = `${this.apiUrl}/update?articleId=${articleId}&sourceBureauId=${sourceBureauId}&quantity=${quantity}`;
    this.http.post<Defectieux>(url, {headers}).subscribe(
      (defectueux: Defectieux) => {
        this.defectueuxCreated = true;
        setTimeout(() => {
          this.defectueuxCreated = false; // Reset the flag after a certain time (e.g., 3 seconds)
          this.closeModal();
          // this.closeModalEvent.emit();
        }, 300);
        this.snackBar.open('Article réparer avec succés', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar'] // Add a custom class to the snackbar
        });
        console.log('Article réparer:', defectueux);
        // Handle any necessary actions upon successful creation
        this.getStock();
       
      },
      (error: any) => {
        this.snackBar.open(error.error.message, 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar'] // Add a custom class to the snackbar
        });
        console.error('Error creating reparation:', error);
        // Handle error if the creation fails
      }
    
      
    );
    
  }
  
  // getBureauxByDepot() {
  //   if (this.selectedDepotValue) {
  //     this.getAllBureauByDepot(this.selectedDepotValue).subscribe((data: any[]) => {
  //       this.bureaux = data.map(item => {
  //         return {
  //           value: item.id,
  //         viewValue: item.name
  //         };
  //       });
  //     });
  //   }
  // }
  ngOnInit() {
    this.getBureau().subscribe((data: any[]) => {
      this.bureaux = data.map(item => {
        return {
          value: item.id,
          viewValue: item.name
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
  }

  


  onSubmit(form: NgForm) {
    // Do something with the form data
  }
  // getStock() {
  //   const url = `${this.baseUrl}/depots/`;
  //   this.http.get(url + this.selectedValue + '/articles').subscribe(
  //     (data: TableData[]) => {
  //       this.tableData = data;
  //     },
  //     (error) => {
  //       console.error(error);
  //       this.tableData = []; // Set the table data to an empty array to clear the table
  //       // Handle the error, e.g. show an error message to the user
  //     }
  //   );
  // }
  // exportToExcel() {
  //   if (this.selectedValue) {
  //     const selectedDepot = this.depots.find(depot => depot.value === this.selectedValue);
  //     const fileName = `${selectedDepot.viewValue}.xlsx`;
  //     const url = `${this.baseUrl}/stock/${this.selectedValue}/export`;
  //     this.http.get(url, {
  //       responseType: 'blob' // Specify the response type as a blob
  //     }).subscribe((response: Blob) => {
  //       // Create a blob URL for the response
  //       const url = window.URL.createObjectURL(response);

  //       // Create a link element and set its attributes
  //       const link = document.createElement('a');
  //       link.href = url;
  //       link.download = fileName;

  //       // Dispatch a click event on the link to trigger the download
  //       link.dispatchEvent(new MouseEvent('click'));

  //       // Clean up the blob URL
  //       window.URL.revokeObjectURL(url);
  //     }, (error) => {
  //       console.error(error);
  //       // Handle the error, e.g. show an error message to the user
  //     });
  //   }
  // }


  exportToExcel() {
    let exportUrl = '';
    let exportFileName = '';
    const headers = this.getHeaders();
    if (this.selectedValue === 'all') {


      if (this.selectedDepotValue) {
        const selectedDepot = this.depots.find(depot => depot.value === this.selectedDepotValue);
        const currentDate = new Date().toLocaleDateString('en-CA', { day: '2-digit', month: '2-digit', year: 'numeric' });
        exportUrl =  `${this.baseUrl}/stock/${this.selectedDepotValue}/exportToPDFWithDefect`;
        exportFileName = `${selectedDepot.viewValue} ${currentDate}.pdf`;
      }
    } else {
      const selectedBureau = this.bureaux.find(depot => depot.value === this.selectedValue);
      const currentDate = new Date().toLocaleDateString('en-CA', { day: '2-digit', month: '2-digit', year: 'numeric' });
      exportUrl = `${this.baseUrl}/stock/${this.selectedValue}/exportBureauToPDF`;
      exportFileName = `${selectedBureau.viewValue} ${currentDate}.pdf`;
    }
  
    if (exportUrl && exportFileName) {
      this.http.get(exportUrl, {
        responseType: 'blob', headers // Specify the response type as a blob
      }).subscribe(
        (response: Blob) => {
          // Create a blob URL for the response
          const url = window.URL.createObjectURL(response);
  
          // Create a link element and set its attributes
          const link = document.createElement('a');
          link.href = url;
          link.download = exportFileName;
  
          // Dispatch a click event on the link to trigger the download
          link.dispatchEvent(new MouseEvent('click'));
  
          // Clean up the blob URL
          window.URL.revokeObjectURL(url);
        },
        (error) => {
          console.error(error);
          // Handle the error, e.g. show an error message to the user
        }
      );
    }
  }

  



  // exportToExcel() {
  //   let exportUrl = '';
  //   let exportFileName = '';
  //   let excelTitle = '';
  
  //   if (this.selectedValue === 'all') {
  //     if (this.selectedDepotValue) {
  //       const selectedDepot = this.depots.find(depot => depot.value === this.selectedDepotValue);
  //       const currentDate = new Date().toLocaleDateString('en-CA', { day: '2-digit', month: '2-digit', year: 'numeric' });
  //       exportUrl =  `${this.baseUrl}/stock/${this.selectedDepotValue}/export`;
  //       exportFileName = `${selectedDepot.viewValue} ${currentDate}.xlsx`;
  //       excelTitle = `Stock for ${selectedDepot.viewValue}`;
  //     }
  //   } else {
  //     const selectedBureau = this.bureaux.find(depot => depot.value === this.selectedValue);
  //     const currentDate = new Date().toLocaleDateString('en-CA', { day: '2-digit', month: '2-digit', year: 'numeric' });
  //     exportUrl = `${this.baseUrl}/stock/${this.selectedValue}/exportBureau`;
  //     exportFileName = `${selectedBureau.viewValue} ${currentDate}.xlsx`;
  //     excelTitle = `Stock for Bureau ${selectedBureau.viewValue}`;
  //   }
  
  //   if (exportUrl && exportFileName) {
  //     this.http.get(exportUrl, {
  //       responseType: 'blob' // Specify the response type as a blob
  //     }).subscribe(
  //       (response: Blob) => {
  //         // Create a blob URL for the response
  //         const url = window.URL.createObjectURL(response);
    
  //         // Create a link element and set its attributes
  //         const link = document.createElement('a');
  //         link.href = url;
  //         link.download = exportFileName;
    
  //         // Dispatch a click event on the link to trigger the download
  //         link.dispatchEvent(new MouseEvent('click'));
    
  //         // Clean up the blob URL
  //         window.URL.revokeObjectURL(url);
    
  //         // Read the file using FileReader
  //         const reader = new FileReader();
  //         reader.onloadend = () => {
  //           const fileData = reader.result as string;
    
  //           const workbook = XLSX.readFile(fileData);
  //           const worksheet = workbook.Sheets[workbook.SheetNames[0]];
  //           const titleCell = { t: 's', v: excelTitle };
  //           worksheet['A1'] = titleCell;
    
  //           const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  //           const updatedExcelData = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    
  //           // Create a new blob URL for the updated Excel file
  //           const updatedUrl = window.URL.createObjectURL(updatedExcelData);
    
  //           // Create a link element and set its attributes for downloading the updated Excel file
  //           const updatedLink = document.createElement('a');
  //           updatedLink.href = updatedUrl;
  //           updatedLink.download = exportFileName;
    
  //           // Dispatch a click event on the updated link to trigger the download of the updated Excel file
  //           updatedLink.dispatchEvent(new MouseEvent('click'));
    
  //           // Clean up the updated blob URL
  //           window.URL.revokeObjectURL(updatedUrl);
  //         };
    
  //         reader.readAsBinaryString(response);
  //       },
  //       (error) => {
  //         console.error(error);
  //         // Handle the error, e.g. show an error message to the user
  //       }
  //     );
  //   }
  // }
    
  

  get articlesToShow(): any[] {
    const startIndex = (this.page - 1) * this.pageSize;
    return this.tableData.slice(startIndex, startIndex + this.pageSize);
  }



  // Navigate to the next page
  nextPage() {
    if (this.page < this.pageCount) {
      this.page++;
    }
  }

  // Navigate to the previous page
  prevPage() {
    if (this.page > 1) {
      this.page--;
    }
  }

  // Return the total number of pages
  get pageCount(): number {
    return Math.ceil(this.tableData.length / this.pageSize);
  }

  getStock() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/depots/ArticleWithQuantityAndBureau/`;
    if (this.selectedValue === 'all') {
      if (this.selectedDepotValue) {
        this.http.get(url + this.selectedDepotValue, {headers}).subscribe(
          (data: TableFullData[]) => {
            this.tableFullData = data;
          },
          (error) => {
            console.error(error);
            this.tableFullData = []; // Set the table data to an empty array to clear the table
            // Handle the error, e.g. show an error message to the user
          }
        );
      } else {
        // Handle the case where no depot is selected
        this.tableFullData = []; // Set the table data to an empty array to clear the table
      }
    } else {
      const url1 = `${this.baseUrl}/stock/bureau/`;
      this.http.get(url1 + this.selectedValue, {headers}).subscribe(
        (data: TableData[]) => {
          this.tableData = data;
        },
        (error) => {
          console.error(error);
          this.tableData = []; // Set the table data to an empty array to clear the table
          // Handle the error, e.g. show an error message to the user
        }
      );
    }
  }

  onChangeBureau() {
    const headers = this.getHeaders();
    if (this.selectedValue === 'all') {
      if (this.selectedDepotValue) {
        const url = `${this.baseUrl}/depots/`;
        this.http.get(url + this.selectedDepotValue + '/articlesWithDefect', {headers}).subscribe(
          (data: TableFullData[]) => {
            this.tableFullData = data;
            this.tableData = []; // Clear the tableData array
          },
          (error) => {
            console.error(error);
            this.tableFullData = []; // Set the table data to an empty array to clear the table
            this.tableData = []; // Clear the tableData array
            // Handle the error, e.g. show an error message to the user
          }
        );
      } else {
        // Handle the case where no depot is selected
        this.tableFullData = []; // Set the table data to an empty array to clear the table
        this.tableData = []; // Clear the tableData array
      }
    } else {
      this.tableFullData = []; // Clear the tableFullData array
      this.getStock(); // Call getStock() to load data for the selected bureau
    }
  }
  getBureauxByDepot() {
    if (this.selectedDepotValue) {
      this.getAllBureauByDepot(this.selectedDepotValue).subscribe(
        (data: any[]) => {
          this.bureaux = data.map(item => {
            return {
              value: item.id,
              viewValue: item.name
            };
          });
          this.selectedValue = 'all'; // Set the selectedValue to 'all' to display all bureaux
          this.onChangeBureau(); // Call the onChangeBureau() function to load the data
        },
        (error) => {
          console.error(error);
          this.bureaux = []; // Set the bureaux array to an empty array
          this.selectedValue = null; // Reset the selectedValue
          this.tableFullData = []; // Set the tableFullData array to an empty array to clear the table
          this.tableData = []; // Clear the tableData array
          // Handle the error, e.g. show an error message to the user
        }
      );
    } else {
      this.bureaux = []; // Set the bureaux array to an empty array
      this.selectedValue = null; // Reset the selectedValue
      this.tableFullData = []; // Set the tableFullData array to an empty array to clear the table
      this.tableData = []; // Clear the tableData array
    }
  }




}