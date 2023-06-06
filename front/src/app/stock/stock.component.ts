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
  tableData: TableData[] = [];
  tableFullData: TableFullData[] = [];
  selectedValue: string;


  constructor(private http: HttpClient) { }
  bureaux: Bureau[] = [];
  getBureau(): Observable<any> {
    return this.http.get('http://localhost:8080/bureau');
  }

  selectedDepotValue: string;
  depots: Bureau[] = [];
  getDepots(): Observable<any> {
    return this.http.get('http://localhost:8080/depots');
  }

  getAllBureauByDepot(depotId: string): Observable<Bureau[]> {
    return this.http.get<Bureau[]>('http://localhost:8080/bureau/' + depotId + '/bureau');
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
    this.http.get('http://localhost:8080/depots/' + this.selectedValue+'/articles').subscribe(
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