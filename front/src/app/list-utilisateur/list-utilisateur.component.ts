import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-utilisateur',
  templateUrl: './list-utilisateur.component.html',
  styleUrls: ['./list-utilisateur.component.css']
})
export class ListUtilisateurComponent implements OnInit {

  private baseUrl = 'http://localhost:8080';
  searchText: string = '';
  tableData: any;
  // Define the page and page size variables
  page = 1;
  pageSize = 10;

  constructor(private http: HttpClient, private snackBar: MatSnackBar, private router: Router) { }

  ngOnInit() {
    this.tableData = []; // Initialize tableData with an empty array
    this.getData().subscribe((data: any[]) => {
      this.tableData = data.filter(item => item.username.toLowerCase().includes(this.searchText.toLowerCase()));
    });
  }

  getData() {
    const url = `${this.baseUrl}/api/auth`;
    return this.http.get<any[]>(url);
  }

  deleteUser(userId: number) {
    const url = `http://localhost:8080/api/auth/${userId}`;
    this.http.delete(url).subscribe(
      response => {
        this.getData();
        console.log(response);
        this.snackBar.open(response['message'], 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar'] // Add a custom class to the snackbar
        });
        
        this.tableData = this.tableData.filter(item => item.id !== userId);
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

  rederiger() {
    
    this.router.navigate(['/signup']);
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
