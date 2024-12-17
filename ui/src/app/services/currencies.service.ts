import {Injectable} from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import {Observable} from "rxjs";
import {Currencies} from "../models/currencies";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CurrenciesService {

  constructor(private httpClient: HttpClient) {  }

  getAvailableCurrencies(): Observable<Currencies> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };

    return this.httpClient.get<Currencies>(`${environment.backendUrl}/currencies`, httpOptions);
  }

}
