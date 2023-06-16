import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { last, lastValueFrom } from 'rxjs'
import { GameData, PlayerInfo, QuarterData } from '../models';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  gameData!: GameData
  q1Data!: QuarterData
  q2Data!: QuarterData 
  q3Data!: QuarterData 
  q4Data!: QuarterData

  games!: GameData[]

  constructor(private http: HttpClient) { }

  getPlayerId(username: string) {
    return lastValueFrom(this.http.get(`/getId/${username}`))
  }

  getPlayerInfo(userId: string): Promise<any> {
    const params = new HttpParams().set('userId', userId); 
    return lastValueFrom( this.http.get('/playerInfo', {params}))
  }

  savePlayerInfo(formData: FormData) {
    return lastValueFrom(this.http.post(`/saveProfile`, formData))
  }

  updatePlayerInfo(info: PlayerInfo, username: string) {
    return lastValueFrom(this.http.post(`/updatePlayerInfo/${username}`, info)); // return Integer on num of rows affected
  }

  updatePhoto(formData: FormData, username: string) {
    return lastValueFrom(this.http.put(`/updatePhoto/${username}`, formData));
  }

  uploadToS3(formData: FormData, game_id: number) {
    const params = new HttpParams().set('game_id', game_id.toString()); 
    return lastValueFrom(this.http.post('/uploadToS3', formData, {params}));
  }

  updateS3UrlToSql(s3Url: string, mediaTypeToUpload: string, gameId : number) {
    return lastValueFrom(this.http.post(`/updateS3UrlToSql/${mediaTypeToUpload}/${gameId}`, s3Url));
  }

  getPlayerProfiles() {
    return lastValueFrom(this.http.get('/playerProfiles'))
  }

  saveGameData(gameData: GameData) {
    return lastValueFrom(this.http.post('/saveGameData', gameData))
  }
  
  saveFullGameData(fullGameData: QuarterData[]) {
    return lastValueFrom(this.http.post('/saveFullGameData', fullGameData))
  }

  getGameDataList() {
    return lastValueFrom(this.http.get('/getGameList')); 
  }
  
  getGameDataById(gameId: number) {
    return lastValueFrom(this.http.get(`/getGameData/${gameId}`))
  }

  getFullGameData(gameId: number) {
    return lastValueFrom(this.http.get(`/getFullGameData/${gameId}`))
  }

  getPlayerStats(userId: string) {
    return lastValueFrom(this.http.get(`/${userId}/stats`))
  }

  initVariables(quarter: string) {
    const qtr = this.getQuarterData(quarter)!; 
    qtr.ownScore = 0; 
    qtr.oppScore = 0; 
    qtr.gaShotIn = 0; 
    qtr.gsShotIn = 0; 
    qtr.gaTotalShots = 0; 
    qtr.gsTotalShots = 0; 
    qtr.ownCpCount = 0; 
    qtr.oppCpCount = 0; 
    qtr.oppSelfError = 0; 
    qtr.goodTeamD = 0; 
    qtr.oppMissShot = 0; 
    qtr.interception = new Map<string, number>([['GS', 0], ['GA', 0], ['WA', 0], ['C', 0], ['WD', 0], ['GD', 0], ['GK', 0]]);
    qtr.lostByIntercept = 0;
    qtr.lostSelfError = 0;
    qtr.quarterSequence = []
  }

  getQuarterData(quarter: string) {
    switch (quarter) {
      case '1': 
        return this.q1Data;
      case '2': 
        return this.q2Data;
      case '3': 
        return this.q3Data;
      case '4': 
        return this.q4Data; 
      default: 
        return undefined; 
    }
  }
}
