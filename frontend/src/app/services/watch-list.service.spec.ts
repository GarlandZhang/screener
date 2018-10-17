import { TestBed, inject } from '@angular/core/testing';

import { WatchListService } from './watch-list.service';

describe('WatchListService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WatchListService]
    });
  });

  it('should be created', inject([WatchListService], (service: WatchListService) => {
    expect(service).toBeTruthy();
  }));
});
