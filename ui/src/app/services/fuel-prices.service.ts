import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {FuelPrices} from "../models/fuel-prices";

@Injectable({
  providedIn: 'root'
})
export class FuelPricesService {
  constructor(private httpClient: HttpClient) {  }

  getFuelPrices(): Observable<FuelPrices> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': 'Basic ' + btoa('user:user')
      })
    };

    return this.httpClient.get<FuelPrices>('http://localhost:8080/fuelprices', httpOptions);
  }
}
