import { Component, OnInit } from '@angular/core';
import {WatchList, WatchListService} from '../services/watch-list.service';

@Component({
  selector: 'app-watch-list',
  templateUrl: './watch-list.component.html',
  styleUrls: ['./watch-list.component.css']
})
export class WatchListComponent implements OnInit {

  constructor(private watchListService: WatchListService) { }
  
  ngOnInit() {
    this.getWatchList();
  }

  getWatchList(): any {
    this.watchListService.getWatchList((watchList: WatchList) => {
      console.log(2);
      console.log(watchList);
    });
  }

}
