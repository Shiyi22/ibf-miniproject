import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs'
import { PlayerInfo } from '../models';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http: HttpClient) { }

  getPlayerId(username: string) {
    return lastValueFrom(this.http.get(`/${username}`))
  }

  getPlayerInfo(userId: string): Promise<any> {
    const params = new HttpParams().set('userId', userId); 
    return lastValueFrom( this.http.get('/playerInfo', {params}))
  }

  updatePlayerInfo(info: PlayerInfo, username: string) {
    return lastValueFrom(this.http.post(`/updatePlayerInfo/${username}`, info)); // return Integer on num of rows affected
  }
}
