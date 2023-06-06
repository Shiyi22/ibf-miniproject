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

  constructor(private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.fullGameData.push(this.backendSvc.q1Data)
    this.fullGameData.push(this.backendSvc.q2Data)
    this.fullGameData.push(this.backendSvc.q3Data)
    this.fullGameData.push(this.backendSvc.q4Data)

    // count interceptions
    let count = 0; 
    this.fullGameData.forEach(quarter => {
      quarter.interceptions.forEach(intercept => {
        count += intercept; 
      })
      this.interceptions.push(count); // there should be 4 counts of interception for each quarter 
    })
  }

}
