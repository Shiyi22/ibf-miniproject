import { Component, OnInit, ViewChild } from '@angular/core';
import { environment } from '../environment';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { StripePaymentElementComponent, StripeService } from 'ngx-stripe';
import { StripeElementsOptions, StripeError, loadStripe } from '@stripe/stripe-js';
import { Backend2Service } from '../services/backend-2.service';
import { PlayerProfile, TeamFund } from '../models';
import { BackendService } from '../services/backend.service';

@Component({
  selector: 'app-teamfunds',
  templateUrl: './teamfunds.component.html',
  styleUrls: ['./teamfunds.component.css']
})
export class TeamfundsComponent implements OnInit {

  teamfunds!: TeamFund[]
  players!: PlayerProfile[]

  // load  Stripe
  stripePromise = loadStripe(environment.stripe);

  constructor(private http: HttpClient, private router: Router, private backend2Svc: Backend2Service, private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.backend2Svc.checkFundsPaid().then((result:any) => {
      this.teamfunds = result;
      console.info('>>> Team funds: ', this.teamfunds)
    })
  }

  refresh() {
    // get list of players 
    this.backendSvc.getPlayerProfiles().then((result:any) => {
      this.players = result;
      // repopulate the list 
      this.backend2Svc.repopulateFundList(this.players).then((result:any) => {
        console.info('>>> Team funds repopulated? ', result);
      })
    })
  }

  async pay(): Promise<void> {
    // create a payment object
    const payment = {
      name: 'team funds',
      currency: 'sgd',
      // amount on cents *10 => to be on dollar
      amount: 2000,
      quantity: '1',
      cancelUrl: 'https://faint-dress-production.up.railway.app/funds',
      successUrl: 'https://faint-dress-production.up.railway.app/funds',
    }

    const stripe = await this.stripePromise;

    // http calls for a backend api
    this.http.post(`${environment.serverUrl}/payment`, payment).subscribe((session: any) => {
      // use stripe to redirect To Checkout page of Stripe platform
      stripe?.redirectToCheckout({sessionId: session.id,})

      // update team funds list paid = true 
      const username = localStorage.getItem('username')!
      this.backendSvc.getPlayerId(username).then((result:any) => {
        const userId = result.userId
        const teamfund: TeamFund = {'id': userId, 'name': username, 'paid': true}
        this.backend2Svc.updateFundList(teamfund).then((result) => {
          console.info('>>> Update team fund ', result)
        })
      })
    })
  }

}
