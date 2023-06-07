import { Component, OnInit } from '@angular/core';
import { BackendService } from '../services/backend.service';
import { QuarterData } from '../models';

@Component({
  selector: 'app-display-stats',
  templateUrl: './display-stats.component.html',
  styleUrls: ['./display-stats.component.css']
})
export class DisplayStatsComponent implements OnInit {

  fullGameData: QuarterData[] = []
  interceptions: number[] = []
  gaPercentage: number[] = []
  gsPercentage: number[] = []

  isSaved: boolean = false 

  constructor(private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.fullGameData.push(this.backendSvc.q1Data)
    this.fullGameData.push(this.backendSvc.q2Data)
    this.fullGameData.push(this.backendSvc.q3Data)
    this.fullGameData.push(this.backendSvc.q4Data)

    // count interceptions
    let count = 0; 
    this.fullGameData.forEach(quarter => {
      quarter.interception.forEach(intercept => {
        count += intercept; 
      })
      this.interceptions.push(count); // there should be 4 counts of interception for each quarter 
      count = 0; // reset to 0 
    })

    // count shooting percentage 
    let ga = 0;
    let gs = 0;
    this.fullGameData.forEach(quarter => {
      ga = (quarter.gaShotIn / quarter.gaTotalShots) * 100
      gs = (quarter.gsShotIn / quarter.gsTotalShots) * 100
      this.gaPercentage.push(ga)
      this.gsPercentage.push(gs)
    })
  }

  saveDB() {
    // convert Map<string, number> to JsonObject first 
    let data: any = {} 
    this.fullGameData.forEach(quarter => {
      quarter.interception.forEach((value, key) => {
        data[key] = value;
      })
      quarter.interceptions = data
      data = {}
      console.info('>>> interceptions object: ', quarter.interceptions)
    })
    // save GameData and fullGameData (4 parts of QuarterData)
    this.backendSvc.saveGameData(this.backendSvc.gameData).then((results) => {
      console.info('>>> Returned response from saving game data: ', results) // game_id : integer 
      // only when game data is saved, then full game data can be inserted/saved
      this.backendSvc.saveFullGameData(this.fullGameData).then((results: any) => {
        console.info('>>> Returned response from saving full game data: ', results) // is saved : boolean
        if (results['is saved'] == 'true')
          this.isSaved = true;
      })
    }) 

  }

}
