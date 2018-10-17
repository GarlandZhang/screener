import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

export interface SymbolList {
  symbols: string[];
}

export interface ScreenIndicators {
  id: number;
  parameterPercentChange: number;
  parameterTimeInterval: string;
  parameterDirection: boolean;
}

export interface Grouping {
  groupId: number;
  userId: number;
  screenIndicatorOutputList: ScreenIndicators[];
}

export interface Groupings {
  groupingOutputList: Grouping[];
}

@Injectable({
  providedIn: 'root'
})
export class ScreenerService {

  private USER_ID = 4;
  private GROUP_ID = 4;
  private screenerUrl = '/group/' + this.GROUP_ID + '/screen/stocks';
  private groupUrl = '/user/' + this.USER_ID + '/groupings';
  constructor(private http: HttpClient) {
  }

  getScreenerResults(callback: (symbols: SymbolList) => any) {
    this.http.get(this.screenerUrl).subscribe((symbolList: SymbolList) => {
      callback(symbolList);
    });
  }

  getGroupings(callback: (groupings: Groupings) => void) {
    this.http.get(this.groupUrl).subscribe((groupings: Groupings) => {
      console.log(groupings);
      callback(groupings);
    });
  }
}
