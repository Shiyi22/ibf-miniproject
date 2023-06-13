import { Component, OnInit, ViewChild } from '@angular/core';
import { environment } from '../environment';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { StripePaymentElementComponent, StripeService } from 'ngx-stripe';
import { StripeElementsOptions, loadStripe } from '@stripe/stripe-js';

@Component({
  selector: 'app-teamfunds',
  templateUrl: './teamfunds.component.html',
  styleUrls: ['./teamfunds.component.css']
})
export class TeamfundsComponent {


  // METHOD 2 
  // @ViewChild(StripePaymentElementComponent)
  // paymentElement!: StripePaymentElementComponent;

  // elementsOptions: StripeElementsOptions = {
  //   locale: 'en',
  //   appearance: {
  //     theme: 'night',
  //     labels: 'floating'
  //   }
  // }

  // constructor(private stripSvc: StripeService, private router: Router, private http: HttpClient) { }

  // ngOnInit(): void {
  //   lastValueFrom(this.http.get('/api/stripe/payment-intent')).then((data:any) => {
  //     this.elementsOptions.clientSecret = data.clientSecret; 
  //   })
  // }

  // pay() {
  //   this.stripSvc.confirmPayment({
  //     elements: this.paymentElement.elements,
  //     redirect: "if_required"
  //   }).subscribe((result:any) => {
  //     if (result.error) {
  //       this.router.navigate(['cancel'])
  //     } else if (result.paymentIntent.status === 'succeeded') {
  //       this.router.navigate(['success'])
  //     }
  //   })
  // }


  // METHOD 1 
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
      cancelUrl: 'http://localhost:4200/cancel',
      successUrl: 'http://localhost:4200/success?id={CHECKOUT_SESSION_ID}',
    }

    const stripe = await this.stripePromise;

    // this is a normal http calls for a backend api
    this.http.post(`${environment.serverUrl}/payment`, payment).subscribe((data: any) => {
      // use stripe to redirect To Checkout page of Stripe platform
      stripe?.redirectToCheckout({sessionId: data.id,})
    })

      // // extract session id from response data
      // const sessionId = data.id;

      // // use session id to get payment intent status 
      // this.http.get(`${environment.serverUrl}/payment-intent/${sessionId}`).subscribe((paymentIntent:any) => {
      //   if (paymentIntent.status === 'succeeded') {
      //     this.router.navigate(['/success'])
      //   } else {
      //     this.router.navigate(['/cancel'])
      //   }
      // })
      // });
  }


}
