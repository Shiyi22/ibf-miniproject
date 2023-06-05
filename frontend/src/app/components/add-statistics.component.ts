import { Component, OnInit } from '@angular/core';
import { BackendService } from '../services/backend.service';
import { count } from 'rxjs';
import { QuarterData } from '../models';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-add-statistics',
  templateUrl: './add-statistics.component.html',
  styleUrls: ['./add-statistics.component.css']
})
export class AddStatisticsComponent implements OnInit {

  timer: string = '00:00'
  countdownDuration: number = this.backendSvc.q1Data.duration * 60; // in seconds 
  quarterStarted = false; 
  quarterEnded = false; 

  currentQ!: string
  opponent!: string
  qData!: QuarterData
  ownCP!: boolean

  constructor(private backendSvc: BackendService, private router: Router, private actRoute: ActivatedRoute) {}

  ngOnInit(): void {  
    // find out which quarter is it 
    this.actRoute.params.subscribe((params) => {
      this.opponent = this.backendSvc.gameData.against
      this.currentQ = params['quarter']
      console.info('>>> current q: ', this.currentQ)
      this.qData = this.backendSvc.getQuarterData(this.currentQ)!
      console.info('>>> qData: ', this.qData)
    })
  }

  addCP(team:string) {
    // add CP count to K or opponent
    if (team.includes('own')) {
      this.qData.ownCpCount ++;
      // change current CP direction
      this.ownCP = true; 
    }
    else {
      // cp belongs to opponent
      this.qData.oppCpCount ++
      this.ownCP = false; 
    }

    // draw xmas tree: O (K or Opponent)

  }

  addIntercept(player : string) {
    const count = this.qData.interceptions.get(player) ?? 0; 
    if (count != undefined)
      this.qData.interceptions.set(player, count + 1); 
    else 
      this.qData.interceptions.set(player, 1);
  }

  addConversion(reason: string) {
    // TODO: include xmas tree navigation

    if (reason.includes('Opp self error')) {
      this.qData.oppSelfError += 1
    } else if (reason.includes('Good team defense')) {
      this.qData.goodTeamD += 1
    } else if (reason.includes('Opp miss shot')) {
      this.qData.oppMissShot += 1
    } else if (reason.includes('Lost on self error')) {
      this.qData.lostSelfError += 1
    } else if (reason.includes('Lost on opp intercept')) {
      this.qData.lostByIntercept += 1
    }
  }

  addScore(playerShotIn: string) {
    // add goal statistics
    if (playerShotIn.includes('GA')) {
      this.qData.gaTotalShots ++; 
      this.qData.gaShotIn ++; 
      this.qData.ownScore ++;
    } else if (playerShotIn.includes('GS')) {
      this.qData.gsShotIn ++;
      this.qData.gsTotalShots ++; 
      this.qData.ownScore ++;
    } else { // opponent scored
      console.info('>>> (before) oppScore: ', this.qData.oppScore)
      this.qData.oppScore ++
      console.info('>>> (after) oppScore: ', this.qData.oppScore)
    }
  
    // draw xmas tree 
  }

  addAttempt(playerAttempted: string) {
    // add attempt statistics 
    if (playerAttempted.includes('GA')) {
      this.qData.gaTotalShots ++;
      console.info('>> GA total shots: ', this.qData.gaTotalShots)
    } else {
      this.qData.gsTotalShots ++; 
    }

    // draw xmas tree 
  }

  saveQuarterData(quarter : string) {
    // save details to backend svc
    switch(quarter) {
      case '1': 
        this.backendSvc.q1Data = this.qData
        break;
      case '2': 
        this.backendSvc.q2Data = this.qData
        break;
      case '3': 
        this.backendSvc.q3Data = this.qData
        break;
      case '4': 
        this.backendSvc.q4Data = this.qData
        break; 
    } 

    // router navigation to key in next Quarter details 
    if (!this.currentQ.match('4'))
      this.router.navigate(['/addGame'])
    else 
      this.router.navigate(['/displayStats'])
  }

  startTimer() {
    this.quarterStarted = true; 
    let duration = this.countdownDuration
    const interval = setInterval(() => {
      const mins = Math.floor(duration / 60)
      const secs = duration % 60
      this.timer = `${this.padNumber(mins)}: ${this.padNumber(secs)}`
      duration --; 
      if (duration < 0) {
        clearInterval(interval)
        // end quarter - stop button pressing 
        this.quarterEnded = true;
      }
    }, 1000)
  }

  padNumber(number: number) {
    return number.toString().padStart(2, '0');
  }

}
