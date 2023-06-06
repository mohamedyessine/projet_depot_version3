import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as JsBarcode from 'jsbarcode';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-liste-article',
  templateUrl: './liste-article.component.html',
  styleUrls: ['./liste-article.component.css']
})
export class ListeArticleComponent implements OnInit {
  searchText: string = '';
  tableData: any;

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.getData().subscribe((data: any[]) => {
      this.tableData = data.filter(item => item.name.toLowerCase().includes(this.searchText.toLowerCase()));
    });
  }

  getData() {
    return this.http.get<any[]>('http://localhost:8080/articles');
  }

  deleteData(id: number) {
    this.http.delete(`http://localhost:8080/articles/${id}`).subscribe(
      response => {
        console.log(response);
        this.snackBar.open(response['message'], 'Close', { 
          duration: 3000,
          panelClass: ['success-snackbar'] // Add a custom class to the snackbar
        });
        this.tableData = this.tableData.filter(item => item.id !== id);
      },
      error => {
        console.error(error);
        this.snackBar.open(error.error.message, 'Close', { 
          duration: 3000,
          panelClass: ['error-snackbar'] // Add a custom class to the snackbar
        });
        // Handle error response
      }
    );
  }

  printBarcode(code: string, name: string) {
    // Generate the barcode
    const canvas = document.createElement('canvas');
    JsBarcode(canvas, code, {
      format: 'CODE128',
      height: 64,
      width: 2,
      displayValue: false
    });
  
    // Open a new window and add the barcode to it
    const printWindow = window.open('', '', 'height=1080,width=780'); // Adjusted dimensions for a 64/24 barcode
    printWindow.document.write('<html><head><title>Barcode</title></head><body>');
    printWindow.document.write('<h6 style="text-align:center">Nom Article: ' + name + '</h6>');
    printWindow.document.write('<img style="display:block;margin:auto;" src="' + canvas.toDataURL() + '"/>');
    printWindow.document.write('</body></html>');
    printWindow.document.close();
  
    // Print the window
    printWindow.print();
  }
  filterTable() {
    if (this.searchText === '') {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data;
      });
    } else {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data.filter(item => {
          const values = Object.values(item).join(' ').toLowerCase();
          return values.includes(this.searchText.toLowerCase());
        });
      });
    }
  }
  
}