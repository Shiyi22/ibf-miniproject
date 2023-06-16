import { Component, OnInit } from '@angular/core';
import { PlayerInfo, PlayerProfile } from '../models';
import { BackendService } from '../services/backend.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Backend2Service } from '../services/backend-2.service';
import { LoginService } from '../services/login.service';

@Component({
  selector: 'app-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.css']
})
export class MembersComponent implements OnInit {

  playerProfiles!: PlayerProfile[]
  playerInfo!: PlayerInfo
  captainAddon: boolean = false
  memberForm!: FormGroup
  memberAdded: boolean = false

  constructor(private backendSvc: BackendService, private fb: FormBuilder, private loginSvc: LoginService) {}

  ngOnInit(): void {
    this.backendSvc.getPlayerProfiles().then((result:any) => {
      console.info('>>> Player Profile: ', result)
      this.playerProfiles = result; 
    })

    // get logged in account player's name and position 
    const username = localStorage.getItem('username')!
    this.backendSvc.getPlayerId(username).then((result:any) => {
      const userId = result.userId
      this.backendSvc.getPlayerInfo(userId).then((result:any) => {
        this.playerInfo = result
        if (this.playerInfo.role === 'Captain') {
          this.memberForm = this.fb.group({ email: this.fb.control('', [Validators.required])})
          this.captainAddon = true; 
        }
      })
    })
  }

  addMember() {
    const emailToValidate = this.memberForm.value.email
    console.info('>>> email to validate: ', emailToValidate)
    this.loginSvc.addEmailToMemberList(emailToValidate).then((result:any) => {
      if (result.isSaved == true) {
        this.memberAdded = true;
      }
    })
  }



}
