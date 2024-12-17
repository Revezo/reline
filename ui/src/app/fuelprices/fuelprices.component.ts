import {Component, OnInit} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FuelPricesService} from "../services/fuel-prices.service";
import {CountryFuelPriceData} from "../models/country-fuel-price-data";
import {Currency} from "../models/currency";
import {CurrenciesService} from "../services/currencies.service";
import {AsyncPipe} from "@angular/common";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {map, Observable, startWith} from "rxjs";

@Component({
    selector: 'app-fuelprices',
    imports: [
        FormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatAutocompleteModule,
        ReactiveFormsModule,
        AsyncPipe,
        MatTableModule
    ],
    templateUrl: './fuelprices.component.html',
    styleUrl: './fuelprices.component.css'
})
export class FuelPricesComponent implements OnInit {
  INITIAL_CURRENCY_CODE = 'EUR';

  displayedColumns: string[] = ['country', 'gasolinePrice', 'dieselPrice', 'currency'];
  tableData: CountryFuelPriceData[] = []
  matTableDataSource = new MatTableDataSource(this.tableData);
  currencies: Currency[] = [];

  myControl = new FormControl<string | Currency>('');
  filteredOptions: Observable<Currency[]> | undefined;
  private appliedCountryFilterValue: string | undefined;

  constructor(private fuelPricesService: FuelPricesService, private currenciesService: CurrenciesService) {
    console.log("CONSTRUCTOR")
  }

  ngOnInit() {
    this.fuelPricesService.getFuelPrices('EUR').subscribe(value => {
      console.log(value);
      this.matTableDataSource = new MatTableDataSource(value.values);
    });

    this.matTableDataSource.filterPredicate = (data, filter) => {
      return data.country.toLowerCase().includes(filter) || data.country.toLowerCase().includes(filter);
    }

    this.currenciesService.getAvailableCurrencies().subscribe(value => {
      console.log(value);
      this.currencies = value.values;
      this.filteredOptions = this.myControl.valueChanges.pipe(
        startWith(this.INITIAL_CURRENCY_CODE),
        map(value => {
          console.log("MYCONTROL: " + this.myControl.value)
          const name = typeof value === 'string' ? value : value?.name;
          return name ? this._filter(name as string) : this.currencies.slice();
        }),
      );
    });
  }

  currencySelected() {
    const selectedValue: Currency = <Currency>this.myControl.value;
    this.matTableDataSource = new MatTableDataSource(this.tableData);
    console.log("Currency selected: " + selectedValue);
    this.fuelPricesService.getFuelPrices(selectedValue.code).subscribe(value => {
      console.log(value);
      this.matTableDataSource = new MatTableDataSource(value.values);
      if (this.appliedCountryFilterValue) {
        this.matTableDataSource.filter = this.appliedCountryFilterValue;
      }
    });
  }

  displayFn(currency: Currency): string {
    return currency && currency.name ? currency.name : '';
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.appliedCountryFilterValue = filterValue.trim().toLowerCase();
    this.matTableDataSource.filter = this.appliedCountryFilterValue;
  }

  private _filter(value: string): Currency[] {
    const filterValue = value.toLowerCase();

    return this.currencies.filter(currency => currency.name.toLowerCase().includes(filterValue)
      || currency.code.toLowerCase().includes(filterValue));
  }

}
