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
  buttonSequence: string[] = []
  tableRowCount: number = 0; 

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

  // Add events to button sequence
  handleButtonsClick(events :string) {
    // push buttons records to buttonSequence
    // if cp button is pressed, save sequence in historySeq in backendSvc + clear for new sequence 
    if ((events === 'A-cp' || events === 'B-cp') && (this.buttonSequence != undefined)) {
      // remove id tag 
      if (events === 'A-cp') { // previous was B-cp
        const toRemoveIdTag = document.getElementById('B-cp-row')
        if (toRemoveIdTag)
          toRemoveIdTag.id = ''
      } else if (events === 'B-cp' && this.buttonSequence != undefined) { // previous was A-cp
        const toRemoveIdTag = document.getElementById('A-cp-row')!
        if (toRemoveIdTag)
          toRemoveIdTag.id = ''
      }
      this.qData.quarterSequence.push(this.buttonSequence)
      this.buttonSequence = [] // empty current sequence
      this.buttonSequence.push(events); // push cp event as start
    } else {
      this.buttonSequence.push(events); 
    }
    console.info('>>> button sequence: ', this.buttonSequence)
    console.info('>>> historical quarter sequence: ', this.qData.quarterSequence)
    // draw xmas tree at every new event added
    this.drawStats();
  }

  drawStats() {
    const table = document.getElementById('live-stats-table') as HTMLTableElement
    const row = table.insertRow();
    this.tableRowCount ++; 

    // list all actions for each event
    const event = this.buttonSequence[this.buttonSequence.length-1]
    console.info('>>> current event:', event)
    // account for intercepts 'A-intercept-GS'
    if(event.includes('A-intercept')) {
      console.info('+++ Reached this line of code!')
      if (this.buttonSequence[this.buttonSequence.length-2] === 'B-cp') {
        const specificRow = document.getElementById('B-cp-row') as HTMLTableRowElement
        specificRow.cells[0].innerHTML = '<span class="material-symbols-outlined">west</span>'; 
      } else {
        // left arrow in new line on team A's side
        row.insertCell().innerHTML = '<span class="material-symbols-outlined">west</span>'
      }
      // find out the player who intercepted and add to stats 
      const player = event.split('-')[2] 
      const count = this.qData.interceptions.get(player) || 0
      this.qData.interceptions.set(player, count + 1)
    }

    switch (event) {
      case 'A-cp': 
        // draw a circle
        row.id = 'A-cp-row'
        row.insertCell().innerHTML = '<span class="material-symbols-outlined">sports_volleyball</span>';
        // add CP count to Krypt
        this.qData.ownCpCount ++;
        break; 
      case 'A-score-ga': 
        // write 'GA' and put a tick on the same line 
        row.insertCell().innerHTML = '<span>GA &#10003;</span>'
        // leave a gap in live stats page
        table.insertRow(); 
        // add to score and total shot count
        this.qData.gaTotalShots ++; 
        this.qData.gaShotIn ++; 
        this.qData.ownScore ++;
        break;
      case 'A-attempt-ga': 
        row.insertCell().innerHTML = '<span>GA \u00D7</span>'
        // add to attempt or total shots count 
        this.qData.gaTotalShots ++;
        break; 
      case 'A-score-gs': 
        // write 'GS' and put tick on same line
        row.insertCell().innerHTML = '<span>GS &#10003;</span>'
        // leave a gap in live stats page
        table.insertRow(); 
        // add to score and total shot counts
        this.qData.gsShotIn ++;
        this.qData.gsTotalShots ++; 
        this.qData.ownScore ++;
        break;  
      case 'A-attempt-gs': 
        row.insertCell().innerHTML = '<span>GS \u00D7</span>'
        // add to attempt or total shots count
        this.qData.gsTotalShots ++; 
        break; 
      case 'A-Opp self error': 
        // draw line from team B to team A 
        if (this.buttonSequence[this.buttonSequence.length-2] === 'B-cp') {
          const specificRow = document.getElementById('B-cp-row') as HTMLTableRowElement
          specificRow.cells[0].innerHTML = '<span class="material-symbols-outlined">west</span>'; 
        } else {
          // left arrow in new line on team A's side
          row.insertCell().innerHTML = '<span class="material-symbols-outlined">west</span>'
        }
        // add to stats 
        this.qData.oppSelfError += 1
        break;
      case 'A-Good team defense': 
        // draw line from team B to team A 
        if (this.buttonSequence[this.buttonSequence.length-2] === 'B-cp') {
          const specificRow = document.getElementById('B-cp-row') as HTMLTableRowElement
          specificRow.cells[0].innerHTML = '<span class="material-symbols-outlined">west</span>'; 
        } else {
          // left arrow in new line on team A's side
          row.insertCell().innerHTML = '<span class="material-symbols-outlined">west</span>'
        }
        //stats 
        this.qData.goodTeamD ++;
        break; 
      case 'A-Opp miss shot': 
        if (this.buttonSequence[this.buttonSequence.length-2] === 'B-cp') {
          const specificRow = document.getElementById('B-cp-row') as HTMLTableRowElement
          specificRow.cells[0].innerHTML = '<span class="material-symbols-outlined">west</span>'; 
        } else {
          // left arrow in new line on team A's side
          row.insertCell().innerHTML = '<span class="material-symbols-outlined">west</span>'
        }
        // stats 
        this.qData.oppMissShot ++
        break; 
      case 'B-cp': 
        // draw a circle
        row.id = 'B-cp-row'
        row.insertCell();
        row.insertCell().innerHTML = '<span class="material-symbols-outlined">sports_volleyball</span>';
        // add CP count to Opponent 
        this.qData.oppCpCount ++;
        break;
      case 'B-score': 
        row.insertCell()
        row.insertCell().innerHTML = '<span>&#10003;</span>'
        // leave a gap in live stats page
        table.insertRow(); 
        // add to opp score 
        this.qData.oppScore ++
        break; 
      case 'B-Lost on self error': 
        // draw a right arrow in team B's side (indicate Team B possesion)
        if (this.buttonSequence[this.buttonSequence.length-2] === 'A-cp') {
          const specificRow = document.getElementById('A-cp-row') as HTMLTableRowElement
          specificRow.insertCell().innerHTML = '<span class="material-symbols-outlined">east</span>'
        } 
        else { 
          // right arrow in new line on Team B's side
          row.insertCell(); 
          row.insertCell().innerHTML = '<span class="material-symbols-outlined">east</span>'
        }
        // stats 
        this.qData.lostSelfError ++; 
        break; 
      case 'B-Lost on opp intercept': 
        if (this.buttonSequence[this.buttonSequence.length-2] === 'A-cp') {
          const specificRow = document.getElementById('A-cp-row') as HTMLTableRowElement
          specificRow.insertCell().innerHTML = '<span class="material-symbols-outlined">east</span>'
        } 
        else { 
          // right arrow in new line on Team B's side
          row.insertCell(); 
          row.insertCell().innerHTML = '<span class="material-symbols-outlined">east</span>'
        }
        // stats 
        this.qData.lostByIntercept ++;
        break;
      default: 
        break; 
    } 
    console.info('>>> Total stats this quarter: ', this.qData)
  }

  // addAttempt(playerAttempted: string) {
  //   // add attempt statistics 
  //   if (playerAttempted.includes('GA')) {
  //     this.qData.gaTotalShots ++;
  //     console.info('>> GA total shots: ', this.qData.gaTotalShots)
  //   } else {
  //     this.qData.gsTotalShots ++; 
  //   }
  // }

  // addScore(playerShotIn: string) {
  //   // add goal statistics
  //   if (playerShotIn.includes('GA')) {
  //     this.qData.gaTotalShots ++; 
  //     this.qData.gaShotIn ++; 
  //     this.qData.ownScore ++;
  //   } else if (playerShotIn.includes('GS')) {
  //     this.qData.gsShotIn ++;
  //     this.qData.gsTotalShots ++; 
  //     this.qData.ownScore ++;
  //   } else { // opponent scored
  //     console.info('>>> (before) oppScore: ', this.qData.oppScore)
  //     this.qData.oppScore ++
  //     console.info('>>> (after) oppScore: ', this.qData.oppScore)
  //   }
  // }

  // addCP(team:string) {
  //   // add CP count to K or opponent
  //   if (team.includes('own')) {
  //     this.qData.ownCpCount ++;
  //     // change current CP direction
  //     this.ownCP = true; // ELIMINATED
  //   }
  //   else {
  //     // cp belongs to opponent
  //     this.qData.oppCpCount ++
  //     this.ownCP = false; // ELIMINATED
  //   }
  // }

  // addConversion(reason: string) {
  //   if (reason.includes('Opp self error')) {
  //     this.qData.oppSelfError += 1
  //   } else if (reason.includes('Good team defense')) {
  //     this.qData.goodTeamD += 1
  //   } else if (reason.includes('Opp miss shot')) {
  //     this.qData.oppMissShot += 1
  //   } else if (reason.includes('Lost on self error')) {
  //     this.qData.lostSelfError += 1
  //   } else if (reason.includes('Lost on opp intercept')) {
  //     this.qData.lostByIntercept += 1
  //   }
  // }

  // addIntercept(player : string) {
  //   const count = this.qData.interceptions.get(player) ?? 0; 
  //   if (count != undefined)
  //     this.qData.interceptions.set(player, count + 1); 
  //   else 
  //     this.qData.interceptions.set(player, 1);
  // }

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
