import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { GameData } from '../models';
import { BackendService } from '../services/backend.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-competition',
  templateUrl: './competition.component.html',
  styleUrls: ['./competition.component.css']
})
export class CompetitionComponent implements OnInit {

  displayedColumns = ['game','date', 'visuals']
  games!: GameData[]
  gameIdToAddMedia!: number
  mediaTypeToUpload!: string
  mediaForm!: FormGroup
  isSaved: boolean = false 

  @ViewChild('mediaFile') mediaFile!: ElementRef;

  constructor (private backendSvc: BackendService, private router: Router, private fb: FormBuilder) {}

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

  displayStats(gameId : number) {
    console.info('>> game id: ', gameId);
    this.backendSvc.games = this.games;  
    this.router.navigate(['/displayStats', gameId]); 
  }

  addMedia(gameId : number) {
    this.gameIdToAddMedia = gameId;
    // display add media form page
    this.mediaForm = this.fb.group ({
      mediaType: this.fb.control('photo'), // default is photo
      mediaFile: this.fb.control('')
    })
  }

  getAcceptedFileTypes() {
    if (this.mediaForm.value.mediaType === 'photo') {
      this.mediaTypeToUpload = 'photo'
      return 'image/*';
    } else if (this.mediaForm.value.mediaType === 'video') {
      this.mediaTypeToUpload = 'video'
      return 'video/*';
    } else 
      return ''; 
  }

  uploadMedia() {
    // send to backend to save in S3 
    const formData = new FormData(); 
    formData.set("media", this.mediaFile.nativeElement.files[0])
    this.backendSvc.uploadToS3(formData, this.gameIdToAddMedia).then(async (results:any) => {
      console.info('>>> s3 upload results(url): ', results); 
      // save url in sql (update)
      let r:any = await this.backendSvc.updateS3UrlToSql(results.s3url, this.mediaTypeToUpload, this.gameIdToAddMedia);
      console.info('>>> s3 url updated to database sql: ', r.isUrlUpdated)
      this.isSaved = true; 
    })

  }

}
