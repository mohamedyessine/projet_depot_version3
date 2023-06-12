import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NgForm } from '@angular/forms';

interface Bureau {
  value: string;
  viewValue: string;

}

interface TableData {
  bureau: {
    name: string;
  };
  article: {
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
  private baseUrl = 'http://localhost:8080';
  tableData: TableData[] = [];
  tableFullData: TableFullData[] = [];
  selectedValue: string;
  // Define the page and page size variables
  page = 1;
  pageSize = 10;

  constructor(private http: HttpClient) { }
  bureaux: Bureau[] = [];
  getBureau(): Observable<any> {
    const url = `${this.baseUrl}/bureau`;
    return this.http.get(url);
  }

  selectedDepotValue: string;
  depots: Bureau[] = [];
  getDepots(): Observable<any> {
    const url = `${this.baseUrl}/depots`;
    return this.http.get(url);
  }

  getAllBureauByDepot(depotId: string): Observable<Bureau[]> {
    const url = `${this.baseUrl}/bureau/`;
    return this.http.get<Bureau[]>(url + depotId + '/bureau');
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
  getStock() {
    const url = `${this.baseUrl}/depots/`;
    this.http.get(url + this.selectedValue + '/articles').subscribe(
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
  exportToExcel() {
    if (this.selectedValue) {
      const selectedDepot = this.depots.find(depot => depot.value === this.selectedValue);
      const fileName = `${selectedDepot.viewValue}.xlsx`;
      const url = `${this.baseUrl}/stock/${this.selectedValue}/export`;
      this.http.get(url, {
        responseType: 'blob' // Specify the response type as a blob
      }).subscribe((response: Blob) => {
        // Create a blob URL for the response
        const url = window.URL.createObjectURL(response);

        // Create a link element and set its attributes
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;

        // Dispatch a click event on the link to trigger the download
        link.dispatchEvent(new MouseEvent('click'));

        // Clean up the blob URL
        window.URL.revokeObjectURL(url);
      }, (error) => {
        console.error(error);
        // Handle the error, e.g. show an error message to the user
      });
    }
  }

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

  // getStock() {
  //   if (this.selectedValue === 'all') {
  //     if (this.selectedDepotValue) {
  //       this.http.get('http://localhost:8080/depots/ArticleWithQuantityAndBureau/' + this.selectedDepotValue).subscribe(
  //         (data: TableFullData[]) => {
  //           this.tableFullData = data;
  //         },
  //         (error) => {
  //           console.error(error);
  //           this.tableFullData = []; // Set the table data to an empty array to clear the table
  //           // Handle the error, e.g. show an error message to the user
  //         }
  //       );
  //     } else {
  //       // Handle the case where no depot is selected
  //       this.tableFullData = []; // Set the table data to an empty array to clear the table
  //     }
  //   } else {
  //     this.http.get('http://localhost:8080/stock/bureau/' + this.selectedValue).subscribe(
  //       (data: TableData[]) => {
  //         this.tableData = data;
  //       },
  //       (error) => {
  //         console.error(error);
  //         this.tableData = []; // Set the table data to an empty array to clear the table
  //         // Handle the error, e.g. show an error message to the user
  //       }
  //     );
  //   }
  // }

  // onChangeBureau() {
  //   if (this.selectedValue === 'all') {
  //     if (this.selectedDepotValue) {
  //       this.http.get('http://localhost:8080/depots/ArticleWithQuantityAndBureau/' + this.selectedDepotValue).subscribe(
  //         (data: TableFullData[]) => {
  //           this.tableFullData = data;
  //           this.tableData = []; // Clear the tableData array
  //         },
  //         (error) => {
  //           console.error(error);
  //           this.tableFullData = []; // Set the table data to an empty array to clear the table
  //           this.tableData = []; // Clear the tableData array
  //           // Handle the error, e.g. show an error message to the user
  //         }
  //       );
  //     } else {
  //       // Handle the case where no depot is selected
  //       this.tableFullData = []; // Set the table data to an empty array to clear the table
  //       this.tableData = []; // Clear the tableData array
  //     }
  //   } else {
  //     this.tableFullData = []; // Clear the tableFullData array
  //     this.getStock(); // Call getStock() to load data for the selected bureau
  //   }
  // }
  // getBureauxByDepot() {
  //   if (this.selectedDepotValue) {
  //     this.getAllBureauByDepot(this.selectedDepotValue).subscribe(
  //       (data: any[]) => {
  //         this.bureaux = data.map(item => {
  //           return {
  //             value: item.id,
  //             viewValue: item.name
  //           };
  //         });
  //         this.selectedValue = 'all'; // Set the selectedValue to 'all' to display all bureaux
  //         this.onChangeBureau(); // Call the onChangeBureau() function to load the data
  //       },
  //       (error) => {
  //         console.error(error);
  //         this.bureaux = []; // Set the bureaux array to an empty array
  //         this.selectedValue = null; // Reset the selectedValue
  //         this.tableFullData = []; // Set the tableFullData array to an empty array to clear the table
  //         this.tableData = []; // Clear the tableData array
  //         // Handle the error, e.g. show an error message to the user
  //       }
  //     );
  //   } else {
  //     this.bureaux = []; // Set the bureaux array to an empty array
  //     this.selectedValue = null; // Reset the selectedValue
  //     this.tableFullData = []; // Set the tableFullData array to an empty array to clear the table
  //     this.tableData = []; // Clear the tableData array
  //   }
  // }




}