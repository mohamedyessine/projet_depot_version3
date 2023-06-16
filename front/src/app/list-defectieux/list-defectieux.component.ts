import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-defectieux',
  templateUrl: './list-defectieux.component.html',
  styleUrls: ['./list-defectieux.component.css']
})
export class ListDefectieuxComponent implements OnInit {
  private baseUrl = 'http://localhost:8080';
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

  getData() {
    const url = `${this.baseUrl}/defectueux`;
    return this.http.get<any[]>(url);
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
    if (this.searchText === '') {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data;
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
          return values.includes(this.searchText.toLowerCase());
        });
      });
    }
  }
  get defectieuxToShow(): any[] {
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
