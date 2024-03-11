import {Component} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FuelPricesService} from "../services/fuel-prices.service";
import {CountryFuelPriceData} from "../models/country-fuel-price-data";


@Component({
  selector: 'app-fuelprices',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatTableModule],
  templateUrl: './fuelprices.component.html',
  styleUrl: './fuelprices.component.css'
})

export class FuelPricesComponent {
  displayedColumns: string[] = ['country', 'gasolinePrice', 'dieselPrice', 'currency'];
  tableData: CountryFuelPriceData[] = []
  matTableDataSource = new MatTableDataSource(this.tableData);

  constructor(private fuelPricesService: FuelPricesService) {
    fuelPricesService.getFuelPrices().subscribe(value => {
      console.log(value);
      this.matTableDataSource = new MatTableDataSource(value.values);
    });
    this.matTableDataSource.filterPredicate = (data, filter) => {
      return data.country.toLowerCase().includes(filter) || data.country.toLowerCase().includes(filter);
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.matTableDataSource.filter = filterValue.trim().toLowerCase();
  }
}
