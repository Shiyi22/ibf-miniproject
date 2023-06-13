import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { HomePageComponent } from './components/home-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { LoginPageComponent } from './components/login-page.component';
import { HttpClientModule } from '@angular/common/http';
import { ProfilePageComponent } from './components/profile-page.component';
import { MembersComponent } from './components/members.component';
import { StatisticsComponent } from './components/statistics.component';
import { CompetitionComponent } from './components/competition.component';
import { AddGameComponent } from './components/add-game.component';
import { AddStatisticsComponent } from './components/add-statistics.component';
import { DisplayStatsComponent } from './components/display-stats.component';
import { UsefulLinksComponent } from './components/useful-links.component';
import { TeamfundsComponent } from './components/teamfunds.component';
import { CancelComponent } from './components/cancel.component';
import { SuccessComponent } from './components/success.component';

@NgModule({
  declarations: [
    AppComponent, HomePageComponent, LoginPageComponent, ProfilePageComponent, MembersComponent, StatisticsComponent, CompetitionComponent,
    AddGameComponent, AddStatisticsComponent, DisplayStatsComponent, UsefulLinksComponent, TeamfundsComponent, CancelComponent, SuccessComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    BrowserAnimationsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }), 
    ReactiveFormsModule, 
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
