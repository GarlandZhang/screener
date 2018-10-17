import { Component, OnInit } from '@angular/core';
import {StockQuote, StockQuoteService} from '../../services/stock-quote.service';

@Component({
  selector: 'app-stock-quote',
  templateUrl: './stock-quote.component.html',
  styleUrls: ['./stock-quote.component.css']
})
export class StockQuoteComponent implements OnInit {
  searchTerm: string;
  stockQuote: StockQuote;

  constructor(private stockQuoteService: StockQuoteService) { }

  ngOnInit() {
  }

  getQuote() {
    this.stockQuoteService.getQuote(this.searchTerm, (stockQuote: StockQuote) => {
      this.stockQuote = stockQuote;
    });
  }
}
