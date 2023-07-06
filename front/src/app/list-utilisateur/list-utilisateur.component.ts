import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-utilisateur',
  templateUrl: './list-utilisateur.component.html',
  styleUrls: ['./list-utilisateur.component.css']
})
export class ListUtilisateurComponent implements OnInit {
  @ViewChild('deleteConfirmationModal', { static: false }) deleteConfirmationModal: ElementRef;
  userToDeleteId: string;
  private baseUrl = 'http://41.226.182.130:5000';
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

  getHeaders(): HttpHeaders {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    return new HttpHeaders().set('Authorization', `Bearer ${currentUser?.token}`);
  }

  getData() {
    const headers = this.getHeaders();
    const url = `${this.baseUrl}/api/auth`;
    return this.http.get<any[]>(url, {headers});
  }

   // Open the modal and set the user ID to delete
   confirmDeleteUser(userId: string) {
    this.userToDeleteId = userId;
    this.showDeleteConfirmationModal();
  }
  showDeleteConfirmationModal() {
    const modalElement: HTMLElement = this.deleteConfirmationModal.nativeElement;
    modalElement.classList.add('show');
    modalElement.style.display = 'block';
  }
  // Close the modal
  closeModal() {
    const modalElement: HTMLElement = this.deleteConfirmationModal.nativeElement;
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
    const modalBackdrop = document.getElementsByClassName('modal-backdrop')[0];
    modalBackdrop.parentNode.removeChild(modalBackdrop);
    this.userToDeleteId = null; // Reset the userToDeleteId

  }

  // deleteUser(userId: number) {
  //   const headers = this.getHeaders();
  //   const url = `http://41.226.182.130:5000/api/auth/${userId}`;
  //   this.http.delete(url, {headers}).subscribe(
  //     response => {
  //       this.getData();
  //       console.log(response);
  //       this.snackBar.open(response['message'], 'Close', {
  //         duration: 3000,
  //         panelClass: ['success-snackbar'] // Add a custom class to the snackbar
  //       });
        
  //       this.tableData = this.tableData.filter(item => item.id !== userId);
  //     },
  //     error => {
  //       console.error(error);
  //       this.snackBar.open(error.error.message, 'Close', {
  //         duration: 3000,
  //         panelClass: ['error-snackbar'] // Add a custom class to the snackbar
  //       });
  //       // Handle error response
  //     }
  //   );
    
  // }

  deleteUserConfirmed() {
    const headers = this.getHeaders();
    const url = `http://41.226.182.130:5000/api/auth/${this.userToDeleteId}`;
  
    this.http.delete(url, { headers }).subscribe(
      response => {
        this.getData();
       // console.log(response);
        this.snackBar.open(response['message'], 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
  
        this.tableData = this.tableData.filter(item => item.id !== this.userToDeleteId);
        this.userToDeleteId = null;
        this.closeModal(); // Close the modal after successful deletion
      },
      error => {
        console.error(error);
        this.snackBar.open(error.error.message, 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
  
        this.userToDeleteId = null;
        this.closeModal(); // Close the modal in case of error
      }
    );
  }
  

//   confirmDeleteUser(userId: number) {
//     if (confirm("Are you sure you want to delete this user?")) {
//         // User clicked "OK" on the confirmation popup
//         this.deleteUser(userId);
//     }
// }


  rederiger() {
    
    this.router.navigate(['/signup']);
  }

  filterTable() {
    const searchText = this.searchText.trim(); // Remove leading and trailing spaces
  
    if (searchText === '') {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data;
        this.page = 1; // Reset the page to 1 when clearing the search
      });
    } else {
      this.getData().subscribe((data: any[]) => {
        this.tableData = data.filter(item => {
          const values = Object.values(item).join(' ').toLowerCase();
          return values.includes(searchText.toLowerCase());
        });
        this.page = 1; // Reset the page to 1 when performing a search
      });
    }
  }
  get usersToShow(): any[] {
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
