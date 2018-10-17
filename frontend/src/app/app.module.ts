import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { WatchListComponent } from './watch-list/watch-list.component';

import { WatchListService } from './services/watch-list.service';
import {HttpClientModule} from '@angular/common/http';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'home'},
];

@NgModule({
  declarations: [
    AppComponent,
    WatchListComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule
  ],
  providers: [WatchListService],
  bootstrap: [AppComponent]
})
export class AppModule { }
