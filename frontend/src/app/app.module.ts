import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { WatchListComponent } from './components/watch-list/watch-list.component';

import { WatchListService } from './services/watch-list.service';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatInputModule,
  MatMenuModule,
  MatProgressSpinnerModule, MatSelectModule, MatSortModule, MatTableModule,
  MatToolbarModule
} from '@angular/material';
import {MatFormFieldModule} from '@angular/material/typings/esm5/form-field';
import { StockQuoteComponent } from './components/stock-quote/stock-quote.component';
import {StockQuoteService} from './services/stock-quote.service';
import { ScreenerComponent } from './components/screener/screener.component';
import {ScreenerService} from './services/screener.service';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'home'},
];

@NgModule({
  declarations: [
    AppComponent,
    WatchListComponent,
    StockQuoteComponent,
    ScreenerComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule
  ],
  providers: [WatchListService, StockQuoteService, ScreenerService],
  bootstrap: [AppComponent]
})
export class AppModule { }
