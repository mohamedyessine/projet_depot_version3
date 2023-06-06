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

export const AdminLayoutRoutes: Routes = [
    // {
    //   path: '',
    //   children: [ {
    //     path: 'dashboard',
    //     component: DashboardComponent
    // }]}, {
    // path: '',
    // children: [ {
    //   path: 'userprofile',
    //   component: UserProfileComponent
    // }]
    // }, {
    //   path: '',
    //   children: [ {
    //     path: 'icons',
    //     component: IconsComponent
    //     }]
    // }, {
    //     path: '',
    //     children: [ {
    //         path: 'notifications',
    //         component: NotificationsComponent
    //     }]
    // }, {
    //     path: '',
    //     children: [ {
    //         path: 'maps',
    //         component: MapsComponent
    //     }]
    // }, {
    //     path: '',
    //     children: [ {
    //         path: 'typography',
    //         component: TypographyComponent
    //     }]
    // }, {
    //     path: '',
    //     children: [ {
    //         path: 'upgrade',
    //         component: UpgradeComponent
    //     }]
    // }
    { path: 'dashboard',      component: DashboardComponent },
    { path: 'add-article',    component: AddArticleComponent },
    { path: 'add-depot',     component: AddDepotComponent },
    { path: 'add-bureau',     component: AddBureauComponent },
    { path: 'transfer',     component: TransferComponent },
    { path: 'liste-article',          component: ListeArticleComponent },
    { path: 'achat',           component: AchatComponent },
    { path: 'stock',  component: StockComponent },
    { path: 'historique-transfert',        component: HistoriqueTransfertComponent },
    { path: 'login',        component: LoginComponent },
];
