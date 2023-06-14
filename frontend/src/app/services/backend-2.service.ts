import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { Event } from '../models';

@Injectable({
  providedIn: 'root'
})
export class Backend2Service {

  constructor(private http: HttpClient) { }

  // SERVE NON NETBALL STATS Http Request 

  getCalEvents() {
    return lastValueFrom(this.http.get('/events'))
  }

  storeEventToDB(event: Event) {
    return lastValueFrom(this.http.post('/storeEventToDB', event))
  }
}
