import { Component, OnInit, ViewChild } from '@angular/core';
import { environment } from '../environment';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { StripePaymentElementComponent, StripeService } from 'ngx-stripe';
import { StripeElementsOptions, StripeError, loadStripe } from '@stripe/stripe-js';

@Component({
  selector: 'app-teamfunds',
  templateUrl: './teamfunds.component.html',
  styleUrls: ['./teamfunds.component.css']
})
export class TeamfundsComponent {

  // load  Stripe
  stripePromise = loadStripe(environment.stripe);

  constructor(private http: HttpClient, private router: Router) {}

  async pay(): Promise<void> {

    // create a payment object
    const payment = {
      name: 'team funds',
      currency: 'sgd',
      // amount on cents *10 => to be on dollar
      amount: 2000,
      quantity: '1',
      cancelUrl: 'http://localhost:4200/funds',
      successUrl: 'http://localhost:4200/success?id={CHECKOUT_SESSION_ID}',
    }

    const stripe = await this.stripePromise;

    // http calls for a backend api
    this.http.post(`${environment.serverUrl}/payment`, payment).subscribe((session: any) => {
      // use stripe to redirect To Checkout page of Stripe platform
      stripe?.redirectToCheckout({sessionId: session.id,})
    })
  }

}
