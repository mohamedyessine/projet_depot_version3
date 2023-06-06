import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-historique-transfert',
  templateUrl: './historique-transfert.component.html',
  styleUrls: ['./historique-transfert.component.css']
})
export class HistoriqueTransfertComponent implements OnInit {
  searchText: string = '';
  tableData: any;

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
    return this.http.get<any[]>('http://localhost:8080/transfers');
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