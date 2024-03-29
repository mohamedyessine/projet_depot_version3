import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import * as JsBarcode from 'jsbarcode';
import { MatSnackBar } from '@angular/material/snack-bar';
import html2canvas from 'html2canvas';
import { ArticlesService } from 'app/services/articles.service';

@Component({
  selector: 'app-liste-article',
  templateUrl: './liste-article.component.html',
  styleUrls: ['./liste-article.component.css']
})
export class ListeArticleComponent implements OnInit {
  private baseUrl = 'http://41.226.182.130:5000';
  searchText: string = '';
  tableData: any;
  selectedType: string;
  page = 1;
  pageSize = 10;
  articles: any[];
  constructor(
    private http: HttpClient, 
    private snackBar: MatSnackBar,
    private articleService: ArticlesService) {}

  ngOnInit() {
    this.tableData = []; // Initialize tableData with an empty array
    this.getData().subscribe((data: any[]) => {
      this.tableData = data.filter(item =>
        item.name.toLowerCase().includes(this.searchText.toLowerCase())
      );
    });
  }

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set(
      'Authorization',
      `Bearer ${currentUser?.token}`
    );
  }

  

  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles`;
    return this.http.get<any[]>(url, { headers });
  }

  deleteData(id: number) {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/articles/${id}`;
    this.http.delete(url, { headers }).subscribe(
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

  item = { code: '', article: '' }; // Sample barcode code
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
      height: 60
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
    this.download(jpegDataUrl, 'barcode.jpg');
  }

  generateAndExportBarcode(code: string) {
    const canvas = document.createElement('canvas'); // Create a new canvas element
    JsBarcode(canvas, code);

    html2canvas(canvas).then((canvas) => {
      const jpegDataUrl = canvas.toDataURL('image/jpeg');
      this.download(jpegDataUrl, 'barcode.jpg');
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
 
  filterTable(): Promise<any> {
    const searchText = this.searchText.trim(); // Remove leading and trailing spaces
  
    if (searchText === '') {
      return this.getData().toPromise()
        .then((data: any[]) => {
          this.tableData = data;
          this.page = 1; // Reset the page to 1 when clearing the search
        });
    } else {
      return this.getData().toPromise()
        .then((data: any[]) => {
          this.tableData = data.filter(item => {
            const values = Object.values(item).join(' ').toLowerCase();
            return values.includes(searchText.toLowerCase());
          });
          this.page = 1; // Reset the page to 1 when performing a search
        });
    }
  }
  
  // filterByType(): Promise<any> {
  //   if (this.selectedType === 'All') {
  //     this.tableData = this.tableData;
  //     return Promise.resolve();
  //   } else {
  //     return this.articleService.getByType(this.selectedType).toPromise()
  //       .then((tableData) => {
  //         console.log(tableData);
  //         this.tableData = tableData;
  //         this.page = 1; // Reset the page when filtering by type
  //       });
  //   }
  // }

  // async synchronizeFilters() {
  //   await this.filterTable();
  //   await this.filterByType();
  // }
  
  // // Call the synchronized filters in your event handlers
  // async onSearchChange() {
  //   await this.synchronizeFilters();
  // }

  // async onTypeChange() {
  //   await this.synchronizeFilters();
  // }
  
  

  async searchAll(criteria: {
    searchText: string,
    selectedType: string, 
   
  } = {
    searchText: '',
      selectedType: '', 
    
    }): Promise<void> {
    const searchPromises: Promise<any[]>[] = [];

    if (criteria.selectedType === 'All') {
      // If 'All' is selected, fetch data from getData()
      searchPromises.push(
        this.getData().toPromise()
          .then(response => response || [])
      );
    } else {
      // Fetch data based on the selected type
      try {
        const typeResults = await this.articleService.getByType(criteria.selectedType).toPromise();
        searchPromises.push(Promise.resolve(typeResults || [])); // Handle undefined case
      } catch (error) {
        console.error("Error fetching by type:", error);
      }
    }

   

    if (criteria.searchText) {
  searchPromises.push(
    this.articleService.searchArticles(criteria.searchText).toPromise()
          .then(response => response || []) // Ensure we always have an array
      );
    }

    // Check if no specific criteria were provided and fetch all articles
    if (!criteria.searchText  && !(criteria.selectedType)) {

      searchPromises.push(
        this.getData().toPromise()
          .then(response => response || [])
      );
    }

    // Wait for all search promises to resolve
    try {
      const results = await Promise.all(searchPromises);
      const commonArticles = results.reduce((accumulator, currentResult) => {
        return accumulator.filter(article => currentResult.some(item => item.id === article.id));
      }, results[0]);

      this.tableData = commonArticles;
      console.log(this.tableData);
    } catch (error) {
      console.error("Error occurred:", error);
      this.tableData = [];
    }
  }

  onSearchChange() {
    this.performSearch();
  }

  onTypeChange() {
    this.performSearch();
  }

  private performSearch() {
    const criteria = {
      searchText: this.searchText,
      selectedType: this.selectedType,
    };

    this.searchAll(criteria);
  }
  


  
  exportInventaireToExcel(): void {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/stock/export/articles_with_quantityAndDefectWithPDF`;
    const currentDate = new Date().toLocaleDateString('en-CA', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
    const fileName = `Inventaire ${currentDate}.pdf`;

    this.http.get(url, { responseType: 'blob', headers }).subscribe(
      (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        this.download(url, fileName);
        window.URL.revokeObjectURL(url);
      },
      error => {
        console.error(error);
        // Handle the error, e.g. show an error message to the user
      }
    );
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

  private download(url: string, fileName: string) {
    const anchorElement = document.createElement('a');
    anchorElement.href = url;
    anchorElement.download = fileName;
    anchorElement.click();
  }
}
