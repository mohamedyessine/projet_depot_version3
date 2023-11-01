import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

declare const $: any;
declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
}
export const ROUTES: RouteInfo[] = [
  //{ path: '/dashboard', title: 'Home',  icon: 'dashboard', class: '' },
  { path: '/add-depot', title: 'Ajout Depot',  icon:'business', class: '' },
  { path: '/add-bureau', title: 'Ajout Bureau',  icon:'business', class: '' },
  { path: '/add-article', title: 'Ajout Article',  icon:'add_box', class: '' },
  { path: '/liste-article', title: 'Liste des articles',  icon:'list', class: '' },
  { path: '/achat', title: 'Achat',  icon:'shopping_cart', class: '' },
  { path: '/stock', title: 'Stock',  icon:'storage', class: '' },
  { path: '/transfer', title: 'Transfert',  icon:'local_shipping', class: '' },
  { path: '/historique-transfert', title: 'Transfert Historique',  icon:'history', class: '' },
  { path: '/list-defect', title: 'Défectueux Historique',  icon:'history', class: '' },
  { path: '/liste-users', title: 'Liste des utilisateurs',  icon:'list', class: '' },
  { path: '/login', title: 'Connextion',  icon:'person', class: 'active-pro' },

  //{ path: '/icons', title: 'Icons',  icon:'bubble_chart', class: '' },
  //{ path: '/notifications', title: 'Notifications',  icon:'notifications', class: '' },
  //{ path: '/upgrade', title: 'Upgrade to PRO',  icon:'unarchive', class: 'active-pro' },
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  isAuthenticated: boolean = false;
  menuItems: any[];

  constructor(private authService: AuthService, private router: Router, private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
  
    // Get authentication status
    this.authService.isAuthenticated$.subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
  
      // Check if user is authenticated and modify menu item accordingly
      if (this.isAuthenticated) {
        this.menuItems[this.menuItems.length - 1].title = 'Deconnexion';
        this.menuItems[this.menuItems.length - 1].icon = 'exit_to_app';
        this.menuItems[this.menuItems.length - 1].class = '';
      } else {
        this.menuItems[this.menuItems.length - 1].title = 'Connexion';
        this.menuItems[this.menuItems.length - 1].icon = 'person';
        this.menuItems[this.menuItems.length - 1].class = 'active-pro';
      }
    });
  }
  
  logout() {
    // Update the Login menu item back to Login
    this.menuItems[this.menuItems.length - 1].title = 'Connexion';
    this.menuItems[this.menuItems.length - 1].icon = 'person';
    this.menuItems[this.menuItems.length - 1].class = 'active-pro';
  
    this.authService.logout();
    this.router.navigate(['/login']);
    this.isAuthenticated = this.authService.isAuthenticated();
    this.snackBar.open('Vous avez déconnecté', 'Close', { 
      duration: 3000,
      panelClass: ['error-snackbar'] // Add a custom class to the snackbar
    });
  }



  isMobileMenu() {
    if ($(window).width() > 991) {
      return false;
    }
    return true;
  };
}
