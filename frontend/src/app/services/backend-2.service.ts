import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { Event, Notif } from '../models';

@Injectable({
  providedIn: 'root'
})
export class Backend2Service {

  // save notification message to backend
  notifType!: string
  notifEvent!: string

  constructor(private http: HttpClient) { }

  // SERVE NON NETBALL STATS Http Request 

  getCalEvents() {
    return lastValueFrom(this.http.get('/events'))
  }

  storeEventToDB(event: Event) {
    return lastValueFrom(this.http.post('/storeEventToDB', event))
  }

  saveNotif(notif: Notif) {
    return lastValueFrom(this.http.post('/saveNotification', notif))
  }
}
