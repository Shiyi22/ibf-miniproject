import { Component, OnInit } from '@angular/core';
import { Notif, PlayerInfo } from '../models';
import { Backend2Service } from '../services/backend-2.service';
import { BackendService } from '../services/backend.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  notifType!: string
  notifEvent!: string
  playerInfo!: PlayerInfo
  notif!: Notif

  constructor(private backend2Svc: Backend2Service, private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.notifType = this.backend2Svc.notifType
    this.notifEvent = this.backend2Svc.notifEvent
  }

  sendNotif() {
    // get player name and photo
    const currentUser = localStorage.getItem('username')!
    this.backendSvc.getPlayerId(currentUser).then(async (result:any) => {
      let r = await this.backendSvc.getPlayerInfo(result.userId)
      this.playerInfo = r

      // send notif and save in backend 
      if (this.notifType == 'add') {
        this.notif = {imageUrl: this.playerInfo.playerPhoto, name: this.playerInfo.name, action: 'added a new event to the team schedule', date: new Date()}
      } else if (this.notifType == 'cancel') {
        this.notif = {imageUrl: this.playerInfo.playerPhoto, name: this.playerInfo.name, action: 'cancelled an event on the team schedule', date: new Date()}
      }
      this.backend2Svc.saveNotif(this.notif);
         
    })


    

    // clear backend notifType and notifEvent 
  }

  notifs: Notif[] = [{imageUrl: 'https://material.angular.io/assets/img/examples/shiba1.jpg', name: 'shiyi', action: 'added a new event to the team schedule', date: new Date('2023-06-15')}]

}
