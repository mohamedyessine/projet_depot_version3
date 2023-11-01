import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-historique-transfert',
  templateUrl: './historique-transfert.component.html',
  styleUrls: ['./historique-transfert.component.css']
})
export class HistoriqueTransfertComponent implements OnInit {
  private baseUrl = 'http://localhost:5000';
  searchText: string = '';
  tableData: any;
 // Define the page and page size variables
 page = 1;
 pageSize = 10;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.tableData = []; // Initialize tableData with an empty array
    this.getData().subscribe((data: any[]) => {
      this.tableData = data.filter(item => {
        const values = Object.values(item).join(' ').toLowerCase();
        return values.includes(this.searchText.toLowerCase());
      });
    });
  }

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/transfers`;
    return this.http.get<any[]>(url, {headers});
  }

  filterTable() {
    const searchText = this.searchText.trim().replace(/\s+/g, ' '); // Remove leading, trailing, and extra spaces
  
    if (searchText === '') {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data;
        this.page = 1;
      });
    } else {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data.filter(item => {
          const values = Object.values(item).flatMap(value => {
            if (typeof value === 'object' && value !== null) {
              return Object.values(value);
            }
            return value;
          }).join(' ').toLowerCase();
  
          const sanitizedSearchText = searchText.toLowerCase();
          return values.includes(sanitizedSearchText);
        });
        this.page = 1;
      });
    }
  }
  
  

  get transfersToShow(): any[] {
    const startIndex = (this.page - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.tableData.slice(startIndex, endIndex);
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