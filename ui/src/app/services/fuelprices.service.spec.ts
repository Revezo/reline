import {TestBed} from '@angular/core/testing';

import {FuelPricesService} from './fuel-prices.service';

describe('FuelpricesService', () => {
  let service: FuelPricesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FuelPricesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
