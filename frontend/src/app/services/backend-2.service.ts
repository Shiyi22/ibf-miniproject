import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { last, lastValueFrom } from 'rxjs';
import { EmailRequest, Event, EventData, EventResult, Notif, PlayerProfile, TeamFund } from '../models';

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
    return lastValueFrom(this.http.get('https://net-pro-production.up.railway.app/events'))
  }

  storeEventToDB(event: Event) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/storeEventToDB', event))
  }

  removeEventFromDB(eventId: string) {
    return lastValueFrom(this.http.delete(`https://net-pro-production.up.railway.app/removeEventFromDB/${eventId}`))
  }

  saveNotif(notif: Notif) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/saveNotification', notif))
  }

  getNotifs() {
    return lastValueFrom(this.http.get('https://net-pro-production.up.railway.app/getNotifications'))
  }

  sendEmailUsingSB(req: EmailRequest) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/sendEmail', req))
  }

  checkFundsPaid() {
    return lastValueFrom(this.http.get('https://net-pro-production.up.railway.app/api/teamFundsList'))
  }

  repopulateFundList(players: PlayerProfile[]) {
    players.forEach((player)=> {player.playerPhoto = ''})
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/api/repopulateList', players))
  }

  updateFundList(tf : TeamFund) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/api/updateFundList', tf))
  }

  saveAttendance(data: EventData) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/saveAttendance', data))
  }

  getIndvAttendance(username:string) {
    return lastValueFrom(this.http.get(`https://net-pro-production.up.railway.app/getIndvAttendance/${username}`))
  }

  getGroupAttendance(eventId:string) {
    return lastValueFrom(this.http.get(`https://net-pro-production.up.railway.app/getGroupAttendance/${eventId}`))
  }

  getFundsAmount() {
    return lastValueFrom(this.http.get('https://net-pro-production.up.railway.app/api/getFundsAmount'))
  }

  addFundsAmount(amount:number) {
    return lastValueFrom(this.http.put('https://net-pro-production.up.railway.app/api/addFundsAmount', amount))
  }

}
