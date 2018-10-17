import { TestBed, inject } from '@angular/core/testing';

import { StockQuoteService } from './stock-quote.service';

describe('StockQuoteService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StockQuoteService]
    });
  });

  it('should be created', inject([StockQuoteService], (service: StockQuoteService) => {
    expect(service).toBeTruthy();
  }));
});
