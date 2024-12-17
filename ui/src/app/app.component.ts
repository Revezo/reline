import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {FuelPricesComponent} from "./fuelprices/fuelprices.component";

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, FuelPricesComponent],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'ui';
}
