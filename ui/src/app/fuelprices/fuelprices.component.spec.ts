import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FuelpricesComponent } from './fuelprices.component';

describe('FuelpricesComponent', () => {
  let component: FuelpricesComponent;
  let fixture: ComponentFixture<FuelpricesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FuelpricesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FuelpricesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
