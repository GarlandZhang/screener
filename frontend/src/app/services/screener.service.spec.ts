import { TestBed, inject } from '@angular/core/testing';

import { ScreenerService } from './screener.service';

describe('ScreenerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ScreenerService]
    });
  });

  it('should be created', inject([ScreenerService], (service: ScreenerService) => {
    expect(service).toBeTruthy();
  }));
});
