import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as moment from 'moment';
import 'moment-timezone';
import { lastValueFrom } from 'rxjs';
import { APIResponse, SearchResult, WeatherForecast } from '../models';
import { environment } from '../environment';
import { validate } from 'uuid';

@Component({
  selector: 'app-useful-links',
  templateUrl: './useful-links.component.html',
  styleUrls: ['./useful-links.component.css']
})
export class UsefulLinksComponent implements OnInit {

  weatherUrl: string = 'https://api.data.gov.sg/v1/environment/2-hour-weather-forecast'
  weatherArea!: FormGroup
  areaNames!: string[]
  areaForecast!: WeatherForecast; 

  youtubeSearch!: FormGroup
  searchResults!: SearchResult[]
  keywordSuggestions: string[] = ['netball', 'netball game', 'netball gym exercises', 'netball strengthening', 'netball shooting', 'netball mental prep']

  courtSearch!:FormGroup

  constructor(private http: HttpClient, private fb: FormBuilder) {}

  ngOnInit(): void {
    // weather forecast will be displayed first thing on the links webpage 
    this.runWeatherApi();
    this.youtubeSearch = this.createYoutubeForm(); 
    this.courtSearch = this.createCourtForm(); 
  }

  createCourtForm() {
    return this.fb.group({
      date: this.fb.control('', [Validators.required])
    })
  }

  searchCourt() {
    const date: Date = this.courtSearch.value.date
    // convert from date to 1688054400
    const timestamp = Math.floor(date.getTime() / 1000);
    //redirect to website
    window.open(`https://members.myactivesg.com/facilities/view/activity/522/venue/208?time_from=${timestamp}`, 'blank')
  }

  async searchKeyword() {
    const apiKey = environment.youtubeApiKey;
    const query = this.youtubeSearch.value.keyword
    const params = new HttpParams().append('part', 'snippet')
                                    .append('maxResults', 8)
                                    .append('q', query)
                                    .append('key', apiKey);

    let result = await lastValueFrom(this.http.get('https://youtube.googleapis.com/youtube/v3/search', {params}))
    const apiResponse: APIResponse = result as APIResponse;
    this.searchResults = apiResponse.items.map((item) => {
      const videoUrl = 'https://www.youtube.com/watch?v=' + item.id.videoId;
      const title = item.snippet.title
      const description = item.snippet.description
      const thumbnail = item.snippet.thumbnails.default.url
      return {videoUrl, title, description, thumbnail, publishedAt: item.snippet.publishedAt}
    })
    console.info('>>> search results: ', this.searchResults)
  }

  createYoutubeForm() {
    return this.fb.group({
      keyword: this.fb.control('', [Validators.required])
    })
  }

  runWeatherApi() {
    const sgtMoment: moment.Moment = moment().tz('Asia/Singapore');
    const currentDate = sgtMoment.format('YYYY-MM-DDTHH:mm:ss');
    const params = new HttpParams().set("date_time", currentDate);
    lastValueFrom(this.http.get(this.weatherUrl, {params})).then((response:any) => {
      this.areaNames = response.area_metadata.map((area:any) => area.name)
      // create form to list the area 
      if (!this.weatherArea) {
        this.weatherArea = this.fb.group({
          area: this.fb.control('', [Validators.required])
        })
      }
    })
  }
  
  displayForecast() {
    const areaName = this.weatherArea.value.area 
    const sgtMoment: moment.Moment = moment().tz('Asia/Singapore');
    const currentDate = sgtMoment.format('YYYY-MM-DDTHH:mm:ss');
    const params = new HttpParams().set("date_time", currentDate);
    lastValueFrom(this.http.get(this.weatherUrl, {params})).then((response:any) => {
      this.areaForecast = response.items[0].forecasts.find((forecast: WeatherForecast) => forecast.area === areaName)
      this.areaForecast.start = response.items[0].valid_period.start
      this.areaForecast.end = response.items[0].valid_period.end
    })
  }
}
