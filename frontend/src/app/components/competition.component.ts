import { Component } from '@angular/core';
import { GameData } from '../models';

@Component({
  selector: 'app-competition',
  templateUrl: './competition.component.html',
  styleUrls: ['./competition.component.css']
})
export class CompetitionComponent {

  displayedColumns = ['position', 'game', 'goal','date']
  games!: GameData[]

}
