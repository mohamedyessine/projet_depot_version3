import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-defectieux',
  templateUrl: './list-defectieux.component.html',
  styleUrls: ['./list-defectieux.component.css']
})
export class ListDefectieuxComponent implements OnInit {
  private baseUrl = 'http://localhost:5000';
  searchText: string = '';
  originalTableData: any;
  tableData: any;

    // Define the page and page size variables
    page = 1;
    pageSize = 10;
  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.getData().subscribe((data: any[]) => {
      this.originalTableData = data;
      this.filterTable();
    });
  }

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }
  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/defectueux`;
    return this.http.get<any[]>(url, {headers});
  }

  // filterTable() {
  //   if (this.searchText === '') {
  //     this.tableData = this.originalTableData;
  //   } else {
  //     this.tableData = this.originalTableData.filter(item => {
  //       const values = [...Object.values(item.sourceDepot), ...Object.values(item.article), ...Object.values(item)].join(' ').toLowerCase();
  //       return values.includes(this.searchText.toLowerCase());
  //     });
  //   }
  // }

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
  get defectieuxToShow(): any[] {
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
