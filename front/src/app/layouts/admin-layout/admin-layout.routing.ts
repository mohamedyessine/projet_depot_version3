import { Routes } from '@angular/router';

import { DashboardComponent } from '../../dashboard/dashboard.component';
import { AddArticleComponent } from '../../add-article/add-article.component';
import { AddDepotComponent } from '../../add-depot/add-depot.component';
import { TransferComponent } from '../../transfer/transfer.component';
import { ListeArticleComponent } from '../../liste-article/liste-article.component';
import { AchatComponent } from '../../achat/achat.component';
import { StockComponent } from '../../stock/stock.component';
import { HistoriqueTransfertComponent } from '../../historique-transfert/historique-transfert.component';
import { LoginComponent } from '../../login/login.component';
import { AddBureauComponent } from '../../add-bureau/add-bureau.component';
import { ListDefectieuxComponent } from 'app/list-defectieux/list-defectieux.component';
import { SignupComponent } from 'app/signup/signup.component';
import { ListUtilisateurComponent } from 'app/list-utilisateur/list-utilisateur.component';
import { AuthGuard } from 'app/auth.guard'
export const AdminLayoutRoutes: Routes = [
   

    // { path: 'add-article',    component: AddArticleComponent },
    // { path: 'add-depot',     component: AddDepotComponent },
    // { path: 'add-bureau',     component: AddBureauComponent },
    // { path: 'transfer',     component: TransferComponent },
    // { path: 'liste-article',          component: ListeArticleComponent },
    // { path: 'achat',           component: AchatComponent },
    // { path: 'stock',  component: StockComponent },
    // { path: 'historique-transfert',        component: HistoriqueTransfertComponent },
    // { path: 'list-defect',        component: ListDefectieuxComponent },
    // { path: 'login',        component: LoginComponent },
    // { path: 'signup',        component: SignupComponent },
    // { path: 'liste-users',          component: ListUtilisateurComponent },

    {
        path: 'add-article',
        component: AddArticleComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'add-depot',
        component: AddDepotComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'add-bureau',
        component: AddBureauComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'transfer',
        component: TransferComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'liste-article',
        component: ListeArticleComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'achat',
        component: AchatComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'stock',
        component: StockComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'historique-transfert',
        component: HistoriqueTransfertComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'list-defect',
        component: ListDefectieuxComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'signup',
        component: SignupComponent,
        // canActivate: [AuthGuard]
      },
      {
        path: 'liste-users',
        component: ListUtilisateurComponent,
        canActivate: [AuthGuard]
      }
];
