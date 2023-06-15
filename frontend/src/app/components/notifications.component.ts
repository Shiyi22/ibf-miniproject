import { Component, OnInit } from '@angular/core';
import { EmailRequest, Notif, PlayerInfo, PlayerProfile } from '../models';
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
  notifs: Notif[] = []
  noNotif = {imageUrl: 'https://thumbs.dreamstime.com/b/new-notification-vector-icon-bell-symbol-social-network-illustration-eps-215964085.jpg', name: '', action: 'Zero notifications yet', date: new Date('2023-06-15')}
  emailButton: boolean = false;

  constructor(private backend2Svc: Backend2Service, private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.notifType = this.backend2Svc.notifType
    this.notifEvent = this.backend2Svc.notifEvent

    this.backend2Svc.getNotifs().then((result:any) => {
      if (result.message == 'empty') {
        console.info('>>> Message: ', result.message);
      } else {
        this.notifs = result; 
      }
    })
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
      this.backend2Svc.saveNotif(this.notif).then((result:any) => {
        console.info("Notification saved to backend: ", result.isSaved);

        // update UI by adding new notif to array 
        this.notifs.push(this.notif);

        // clear backend notifType and notifEvent 
        this.backend2Svc.notifType = ''
        this.backend2Svc.notifEvent = ''

        // trigger send email button
        this.emailButton = true;
      })
    })
  }

  async sendEmail() {
    // list of emails to send to 
    const emails: string[] = []
    this.backendSvc.getPlayerProfiles().then((result:any) => {
      const profiles: PlayerProfile[] = result 
      profiles.forEach((profile) => {emails.push(profile.email)})
      console.info('>>> Emails to send to: ', emails)

      // subject 
      const subject = 'New notification from Kryptonite App'
      // body
      const body = 'Message: ' + this.notif.name + ' '+ this.notif.action + '. Please login to the app to see details. ' + '(Date: ' + this.notif.date + ')'

      const emailReq: EmailRequest = {to: emails, subject: subject, body: body}
      this.backend2Svc.sendEmailUsingSB(emailReq).then((result:any) => {
        console.info('>>> Email sent: ', result.emailSent)
      })

    })
  }
}
