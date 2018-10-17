import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {WatchList} from './watch-list.service';

export interface StockQuote {
  symbol: string;
  openPrice: number;
  highPrice: number;
  lowPrice: number;
  currentPrice: number;
  volume: number;
  latestTradingDay: string;
  previousClose: number;
  change: number;
  changePercent: number;
}

@Injectable({
  providedIn: 'root'
})
export class StockQuoteService {
  private stockQuoteUrl = '/quote/';

  constructor(private http: HttpClient) {

  }

  getQuote(tickerSymbol: string, callback: (StockQuote) => any): any {
    this.http.get(this.stockQuoteUrl + tickerSymbol).subscribe((stockQuote: StockQuote) => {
      callback(stockQuote);
    });
  }
}
