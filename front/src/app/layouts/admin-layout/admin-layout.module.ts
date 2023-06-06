import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdminLayoutRoutes } from './admin-layout.routing';
import { DashboardComponent } from '../../dashboard/dashboard.component';
import { AddArticleComponent } from '../../add-article/add-article.component';
import { AddDepotComponent } from '../../add-depot/add-depot.component';
import { TransferComponent } from '../../transfer/transfer.component';
import { ListeArticleComponent } from '../../liste-article/liste-article.component';
import { AchatComponent } from '../../achat/achat.component';
import { StockComponent } from '../../stock/stock.component';
import { HistoriqueTransfertComponent } from '../../historique-transfert/historique-transfert.component';
import { LoginComponent } from '../../login/login.component';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatRippleModule} from '@angular/material/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatSelectModule} from '@angular/material/select';
import { AddBureauComponent } from '../../add-bureau/add-bureau.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(AdminLayoutRoutes),
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatRippleModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule,
  ],
  declarations: [
    DashboardComponent,
    AddArticleComponent,
    AddDepotComponent,
    TransferComponent,
    ListeArticleComponent,
    AchatComponent,
    StockComponent,
    HistoriqueTransfertComponent,
    LoginComponent,
    AddBureauComponent
  ]
})

export class AdminLayoutModule {}
