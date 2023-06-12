import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as moment from 'moment';
import 'moment-timezone';
import { lastValueFrom } from 'rxjs';
import { WeatherForecast } from '../models';

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

  constructor(private http: HttpClient, private fb: FormBuilder) {}

  ngOnInit(): void {
    // weather forecast will be displayed first thing on the links webpage 
    this.runWeatherApi();
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
