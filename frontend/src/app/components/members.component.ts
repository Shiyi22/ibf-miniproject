import { Component, OnInit } from '@angular/core';
import { PlayerProfile } from '../models';
import { BackendService } from '../services/backend.service';

@Component({
  selector: 'app-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.css']
})
export class MembersComponent implements OnInit {

  playerProfiles!: PlayerProfile[]

  constructor(private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.backendSvc.getPlayerProfiles().then((result:any) => {
      console.info('>>> Player Profile: ', result)
      this.playerProfiles = result; 
    })
  }



}
