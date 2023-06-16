import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app.routing';
import { ComponentsModule } from './components/components.module';
import { AppComponent } from './app.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import {MatSelectModule} from '@angular/material/select';
import { AuthService } from './auth.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { WelcomeComponent } from './welcome/welcome.component';
import { ListDefectieuxComponent } from './list-defectieux/list-defectieux.component';


@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ComponentsModule,
    RouterModule,
    AppRoutingModule,
    MatSnackBarModule,
    MatSelectModule
    
  ],
  declarations: [			
    AppComponent,
    AdminLayoutComponent,
      WelcomeComponent,
      ListDefectieuxComponent
   ],
  providers: [
    AuthService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
