import { AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { BackendService } from '../services/backend.service';
import { CP, GameData, QuarterData } from '../models';
import { ActivatedRoute, Router } from '@angular/router';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-display-stats',
  templateUrl: './display-stats.component.html',
  styleUrls: ['./display-stats.component.css']
})
export class DisplayStatsComponent implements OnInit, AfterViewInit {

  fullGameData: QuarterData[] = []
  interceptions: number[] = []
  gaPercentage: number[] = []
  gsPercentage: number[] = []
  centerpass: CP[] = []

  @ViewChildren('doughnutChart') doughnutChartRefs!: QueryList<ElementRef<HTMLCanvasElement>>;

  isSaved: boolean = false 
  gameId!: number; 
  game!: GameData
  isDataLoaded: boolean = false; 

  constructor(private backendSvc: BackendService, private router: Router, private actRoute: ActivatedRoute) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      if (this.isDataLoaded) {
        console.info('>>> center pass stats: ', this.centerpass);
        this.createDoughnutCharts();
      }
    }, 300)
  }

  ngOnInit(): void {
    this.actRoute.params.subscribe(async (params) => {
      this.gameId = params['gameId']
      // retrieve from backend (click on game )
      if (this.gameId) {
        try {
          const results:any = await this.backendSvc.getFullGameData(this.gameId)
          console.info('>>> results: ', results)
          this.fullGameData = results; 
          // convert interceptions into interception 
          this.fullGameData.forEach((qtr:any) => {
            const interceptObject = JSON.parse(qtr.interceptions)
            qtr.interception = new Map<string, number>(); 
            for (const key in interceptObject) {
              if (interceptObject.hasOwnProperty(key)) {
                qtr.interception.set(key, interceptObject[key]);
              }
            }
          })
          console.info('>>> full game data retrieved: ', results); 
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
          // after loading data => count CP, neat, returned, loss 
          this.countStats();
          this.isDataLoaded = true; 
        } catch (error) {
          console.error('Error: ', error)
        }
        // retrieve gamedata
        this.backendSvc.getGameDataById(this.gameId).then((results:any) => {
          console.info('>>> game data by id: ', results)
          this.game = results
        })    
      } else {
        // from adding game stats str to displaying it 
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

        // after loading data => count CP, neat, returned, loss 
        this.countStats();
      }
    })
  }

  countStats() {
    this.fullGameData.forEach(quarter => {
      let cp: CP = {count: 0, neat: 0, returned: 0, lost: 0}
      // remove empty[]
      quarter.quarterSequence.shift()
      quarter.quarterSequence.forEach((sequence) => {
        // count CP if its Kryptonite's 
        if (sequence[0].startsWith('A-cp')) {
          cp.count ++;
          // neat case 
          if (sequence.every((item) => !item.startsWith('B-')))
            cp.neat ++;
          // returned cas e
          if (sequence.some((item) => item.startsWith('B-')) && sequence.slice(-1)[0].startsWith('A-score'))
            cp.returned ++;
          if (sequence.some((item) => item.startsWith('B-')) && sequence.slice(-1)[0].startsWith('B-score'))
            cp.lost ++;
        }
      })
      // add to CP[]
      this.centerpass.push(cp);
    })
  }
    
  createDoughnutCharts() {
    console.info('doughnutChartRefs length:', this.doughnutChartRefs.length);
    console.info('Centerpass length:', this.centerpass.length);

    this.doughnutChartRefs.toArray().forEach((doughnutChartRef, index) => {
      const cp = this.centerpass[index];
      const doughnutChart = new Chart(doughnutChartRef.nativeElement.getContext('2d')!, {
        type: 'doughnut',
        data: {
          labels: ['Neat', 'Returned', 'Lost'],
          datasets: [{
            data: [cp.neat, cp.returned, cp.lost],
            backgroundColor: ['green', 'yellow', 'red'],
          }]
        },
        options: { 
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: `Total center passes: ${cp.count}`
            }
          } 
        }
      });
    });
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
        console.info(">>> is saved: ", results.isSaved);
        if (results.isSaved == 'true')
          this.isSaved = true;

        this.router.navigate(['/games']); 
      })
    }) 

  }

}
