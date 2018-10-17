import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

interface WatchedTickerOutput {
  ticker: string;
  title: string;
}

export interface WatchList {
  tickers: WatchedTickerOutput[];
}

@Injectable({
  providedIn: 'root'
})
export class WatchListService {

  private watchListUrl = '/watch-list/user/';

  private USER_ID = 4;
  
  constructor(private http: HttpClient) {

  }

  getWatchList(callback: (WatchList) => any): any {
    this.http.get(this.watchListUrl + this.USER_ID).subscribe((watchList: WatchList) => {
      console.log(watchList);
      callback(watchList);
    });
  }
}
