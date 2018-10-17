import { Component, OnInit } from '@angular/core';
import {Grouping, Groupings, ScreenerService, SymbolList} from '../../services/screener.service';

@Component({
  selector: 'app-screener',
  templateUrl: './screener.component.html',
  styleUrls: ['./screener.component.css']
})
export class ScreenerComponent implements OnInit {

  constructor(private screenerService: ScreenerService) {
    this.getGroupings();
  }

  symbolList: SymbolList;
  groupings: Groupings;
  
  ngOnInit() {
  }

  getGroupings() {
    this.screenerService.getGroupings((groupings: Groupings) => {
      this.groupings = groupings;
      console.log(this.groupings);
    });
  }
  
  
  getScreenerResults() {
    this.screenerService.getScreenerResults((symbolList: SymbolList) => {
      this.symbolList = symbolList;
    });
  }
}
