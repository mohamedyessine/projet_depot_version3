import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as JsBarcode from 'jsbarcode';
import { MatSnackBar } from '@angular/material/snack-bar';
import html2canvas from 'html2canvas';
import { saveAs } from 'file-saver';
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

  // printBarcode(code: string, name: string) {
  //   // Generate the barcode
  //   const canvas = document.createElement('canvas');
  //   JsBarcode(canvas, code, {
  //     format: 'CODE128',
  //     height: 64,
  //     width: 2,
  //     displayValue: false
  //   });
  
  //   // Open a new window and add the barcode to it
  //   const printWindow = window.open('', '', 'height=1080,width=780'); // Adjusted dimensions for a 64/24 barcode
  //   printWindow.document.write('<html><head><title>Barcode</title></head><body>');
  //   printWindow.document.write('<h6 style="text-align:center">Nom Article: ' + name + '</h6>');
  //   printWindow.document.write('<img style="display:block;margin:auto;" src="' + canvas.toDataURL() + '"/>');
  //   printWindow.document.write('</body></html>');
  //   printWindow.document.close();
  
  //   // Print the window
  //   printWindow.print();
  // }
  item = { code: '' }; // Sample barcode code

  ngAfterViewInit() {
    this.generateBarcode(this.item.code);
  }
  // generateBarcode(code: string) {
  //   const canvas = document.getElementById('barcode') as HTMLCanvasElement;
  //   if (canvas) {
  //     JsBarcode(canvas, code);
  //   } else {
  //     console.error('Canvas element with id "barcode" not found.');
  //   }
  // }
//   generateBarcode(code: string) {
//     const canvas = document.getElementById('barcode') as HTMLCanvasElement;
//     if (canvas) {
//       JsBarcode(canvas, code);
//       this.exportBarcode(); // Call exportBarcode() after generating the barcode
//     } else {
//       console.error('Canvas element with id "barcode" not found.');
//     }
//   }
//   exportBarcode() {
//   const canvas = document.getElementById('barcode') as HTMLCanvasElement;
//   if (canvas) {
//     const targetWidth = 300; // Desired width in millimeters
//     const targetHeight = 100; // Desired height in millimeters

//     const scale = targetWidth / canvas.offsetWidth;

//     html2canvas(canvas, {
//       scale: scale
//     }).then((canvas) => {
//       const jpegDataUrl = canvas.toDataURL('image/jpeg');
//       saveAs(jpegDataUrl, 'barcode.jpg');
//     });
//   } else {
//     console.error('Canvas element with id "barcode" not found.');
//   }
// }
generateBarcode(code: string) {
  const canvas = document.createElement('canvas');
  JsBarcode(canvas, code);
  this.exportBarcode(canvas);
}


exportBarcode(canvas: HTMLCanvasElement) {
  if (canvas) {
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

    // Export the resized canvas as an image
    const jpegDataUrl = resizedCanvas.toDataURL('image/jpeg');
    saveAs(jpegDataUrl, 'barcode.jpg');
    
  } else {
    console.error('Canvas element not found.');
  }
}



 generateAndExportBarcode(code: string) {
    const canvas = document.createElement('canvas'); // Create a new canvas element
    JsBarcode(canvas, code);

    html2canvas(canvas).then((canvas) => {
      const jpegDataUrl = canvas.toDataURL('image/jpeg');
      saveAs(jpegDataUrl, 'barcode.jpg');
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
  
}