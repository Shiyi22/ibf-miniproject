import { Component, OnInit } from '@angular/core';
import { GameData } from '../models';
import { BackendService } from '../services/backend.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-competition',
  templateUrl: './competition.component.html',
  styleUrls: ['./competition.component.css']
})
export class CompetitionComponent implements OnInit {

  displayedColumns = ['game','date']
  games!: GameData[]

  constructor (private backendSvc: BackendService, private router: Router) {}

  ngOnInit(): void {
    this.backendSvc.getGameDataList().then((results: any) => {
      console.info('>>> results: ', results)
      if (results.message === 'no game records') {
        // do nth
      } else {
        this.games = results; 
      }
    })
  }

  displayStats(gameId : string) {
    console.info('>> game id: ', gameId);
    this.router.navigate(['/displayStats', gameId]); 
  }

}
