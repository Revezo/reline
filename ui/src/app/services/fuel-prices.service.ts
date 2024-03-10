import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class FuelPricesService {
  constructor(private httpClient: HttpClient) {  }

  getFuelPrices() {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': 'Basic ' + btoa('user:user')
      })
    };

    return this.httpClient.get('http://localhost:8080/fuelprices', httpOptions);
  }
}
