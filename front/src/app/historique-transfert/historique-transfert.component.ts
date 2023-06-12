import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-historique-transfert',
  templateUrl: './historique-transfert.component.html',
  styleUrls: ['./historique-transfert.component.css']
})
export class HistoriqueTransfertComponent implements OnInit {
  private baseUrl = 'http://localhost:8080';
  searchText: string = '';
  tableData: any;
 // Define the page and page size variables
 page = 1;
 pageSize = 10;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.getData().subscribe((data: any[]) => {
      this.tableData = data.filter(item => {
        const values = Object.values(item).join(' ').toLowerCase();
        return values.includes(this.searchText.toLowerCase());
      });
    });
  }

  getData() {
    const url = `${this.baseUrl}/transfers`;
    return this.http.get<any[]>(url);
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

  get transfersToShow(): any[] {
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