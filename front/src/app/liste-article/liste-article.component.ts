import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import * as JsBarcode from 'jsbarcode';
import { MatSnackBar } from '@angular/material/snack-bar';
import html2canvas from 'html2canvas';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-liste-article',
  templateUrl: './liste-article.component.html',
  styleUrls: ['./liste-article.component.css']
})
export class ListeArticleComponent implements OnInit {
  private baseUrl = 'http://localhost:8080';
  searchText: string = '';
  tableData: any;
  // Define the page and page size variables
  page = 1;
  pageSize = 10;

  constructor(private http: HttpClient, private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.tableData = []; // Initialize tableData with an empty array
    this.getData().subscribe((data: any[]) => {
      this.tableData = data.filter(item => item.name.toLowerCase().includes(this.searchText.toLowerCase()));
    });
  }
  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }
  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles`;
    return this.http.get<any[]>(url, {headers});
  }

  deleteData(id: number) {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles/${id}`;
    this.http.delete(url, {headers}).subscribe(
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




  item = { code: '', article:'' }; // Sample barcode code
  ngAfterViewInit() {
    this.generateBarcode(this.item.code, this.item.article);
  }
  
  generateBarcode(code: string, name: string) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const barcodeOptions = {
      text: code + '\n' + name, // Combine code and name with a line break
      fontOptions: 'bold',
      textAlign: 'center',
      fontSize: 14,
      width: 2,
      height: 60,
    };
    JsBarcode(canvas, code, barcodeOptions);
  
    const targetWidth = 440; // Desired width in millimeters
    const targetHeight = 200; // Desired height in millimeters
  
    const scale = targetWidth / canvas.offsetWidth;
  
    // Create a new hidden canvas element to perform the resizing
    const resizedCanvas = document.createElement('canvas');
    resizedCanvas.width = targetWidth;
    resizedCanvas.height = targetHeight;
    const resizedContext = resizedCanvas.getContext('2d');
  
    // Resize the original canvas content to the target size
    resizedContext.drawImage(
      canvas,
      0,
      0,
      canvas.width,
      canvas.height,
      0,
      0,
      targetWidth,
      targetHeight
    );
  
    // Create a new container element for the barcode and name
    const container = document.createElement('div');
    container.style.textAlign = 'center';
  
    // Append the resized barcode canvas to the container
    container.appendChild(resizedCanvas);
  
    // Create a new h3 element for the name
    const nameElement = document.createElement('h3');
    nameElement.style.textAlign = 'center';
    nameElement.innerText = name;
    container.appendChild(nameElement);
  
    // Export the container as an image
    const jpegDataUrl = resizedCanvas.toDataURL('image/jpeg');
    FileSaver.saveAs(jpegDataUrl, 'barcode.jpg');
  }
  

  generateAndExportBarcode(code: string) {
    const canvas = document.createElement('canvas'); // Create a new canvas element
    JsBarcode(canvas, code);

    html2canvas(canvas).then((canvas) => {
      const jpegDataUrl = canvas.toDataURL('image/jpeg');
      FileSaver.saveAs(jpegDataUrl, 'barcode.jpg');
    });
  }



  printBarcode(code: string) {
    // Retrieve the barcode canvas element
    const canvas = document.getElementById('barcode') as HTMLCanvasElement;
    JsBarcode(canvas, code);

    // Convert the barcode canvas to a JPEG image
    const jpegUrl = canvas.toDataURL('image/jpeg');

    // Create a new window and add the barcode image to it
    const printWindow = window.open('', '', 'height=600,width=800');
    printWindow.document.write('<html><head><title>Barcode</title></head><body>');
    printWindow.document.write('<img src="' + jpegUrl + '"/>');
    printWindow.document.write('</body></html>');
    printWindow.document.close();

    // Print the window
    printWindow.addEventListener('load', function () {
      printWindow.print();
      printWindow.close();
    });
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

  exportInventaireToExcel(): void {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/stock/export/articles_with_quantityAndDefectWithPDF`;
    const currentDate = new Date().toLocaleDateString('en-CA', { day: '2-digit', month: '2-digit', year: 'numeric' });
    const fileName = `Inventaire ${currentDate}.pdf`;

    this.http.get(url, { responseType: 'blob', headers }).subscribe((response: Blob) => {
      const url = window.URL.createObjectURL(response);
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      link.dispatchEvent(new MouseEvent('click'));
      window.URL.revokeObjectURL(url);
    }, (error) => {
      console.error(error);
      // Handle the error, e.g. show an error message to the user
    });
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


}
