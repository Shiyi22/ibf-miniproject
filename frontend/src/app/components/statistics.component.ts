import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendService } from '../services/backend.service';
import { PlayerInfo, PlayerStats } from '../models';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  userId!: string
  playerInfo!: PlayerInfo
  playerStats!: PlayerStats

  constructor(private actRoute: ActivatedRoute, private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.actRoute.params.subscribe(async (params) => {
      this.userId = params['userId']
      // get player info and player position 
      let r = await this.backendSvc.getPlayerInfo(this.userId)
      this.playerInfo = r; 
      
      // get player stats 
      let s:any = await this.backendSvc.getPlayerStats(this.userId); 
      this.playerStats  = s; 
    })
  }

  capName(name:string) {
    return name.toUpperCase();
  }

}
