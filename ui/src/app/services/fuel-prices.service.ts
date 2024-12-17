import {Injectable} from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import {Observable} from "rxjs";
import {FuelPrices} from "../models/fuel-prices";
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FuelPricesService {
  constructor(private httpClient: HttpClient) {  }

  getFuelPrices(currencyCode: string): Observable<FuelPrices> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': 'Basic ' + btoa('user:user')
      }),
      params: new HttpParams().set('currencyCode', currencyCode)
    };

    return this.httpClient.get<FuelPrices>(`${environment.backendUrl}/fuelprices`, httpOptions);
  }
}
