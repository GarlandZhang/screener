import { Component, OnInit } from '@angular/core';
import {WatchList, WatchListService} from '../../services/watch-list.service';
import {MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-watch-list',
  templateUrl: './watch-list.component.html',
  styleUrls: ['./watch-list.component.css']
})
export class WatchListComponent implements OnInit {

  public watchList: WatchList;

  constructor(private watchListService: WatchListService) { }
  
  ngOnInit() {
    this.getWatchList();
  }

  getWatchList(): any {
    this.watchListService.getWatchList((watchList: WatchList) => {
      this.watchList = watchList;
    });
  }

  importWatchList() {
    this.watchListService.importWatchListFromStockTwits((watchList: WatchList) => {
      this.watchList = watchList;
    });
  }
}
