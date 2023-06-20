import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { last, lastValueFrom } from 'rxjs';
import { EmailRequest, Event, Notif, PlayerProfile, TeamFund } from '../models';

@Injectable({
  providedIn: 'root'
})
export class Backend2Service {

  // save notification message to backend
  notifType: string = ''
  notifEvent: string = ''

  constructor(private http: HttpClient) { }

  // SERVE NON NETBALL STATS Http Request 

  getCalEvents() {
    return lastValueFrom(this.http.get('/events'))
  }

  storeEventToDB(event: Event) {
    return lastValueFrom(this.http.post('/storeEventToDB', event))
  }

  removeEventFromDB(eventId: string) {
    return lastValueFrom(this.http.delete(`/removeEventFromDB/${eventId}`))
  }

  saveNotif(notif: Notif) {
    return lastValueFrom(this.http.post('/saveNotification', notif))
  }

  getNotifs() {
    return lastValueFrom(this.http.get('/getNotifications'))
  }

  sendEmailUsingSB(req: EmailRequest) {
    return lastValueFrom(this.http.post('/sendEmail', req))
  }

  checkFundsPaid() {
    return lastValueFrom(this.http.get('/api/teamFundsList'))
  }

  repopulateFundList(players: PlayerProfile[]) {
    players.forEach((player)=> {player.playerPhoto = ''})
    return lastValueFrom(this.http.post('/api/repopulateList', players))
  }

  updateFundList(tf : TeamFund) {
    return lastValueFrom(this.http.post('/api/updateFundList', tf))
  }
}
