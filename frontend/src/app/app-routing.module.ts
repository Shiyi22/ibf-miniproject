import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { HomePageComponent } from './components/home-page.component';
import { LoginPageComponent } from './components/login-page.component';
import { NotificationsComponent } from './components/notifications.component';
import { ProfilePageComponent } from './components/profile-page.component';
import { MembersComponent } from './components/members.component';
import { StatisticsComponent } from './components/statistics.component';
import { CompetitionComponent } from './components/competition.component';
import { AddGameComponent } from './components/add-game.component';
import { AddStatisticsComponent } from './components/add-statistics.component';
import { DisplayStatsComponent } from './components/display-stats.component';

const routes: Routes = [
  {path: '', component: HomePageComponent},
  {path: 'login', component: LoginPageComponent},
  {path: 'notifications', component: NotificationsComponent},
  {path: 'profile', component: ProfilePageComponent},
  {path: 'members', component: MembersComponent},
  {path: 'statistics/:userId', component: StatisticsComponent},
  {path: 'games', component: CompetitionComponent},
  {path: 'addGame', component: AddGameComponent}, 
  {path: 'addStats/:quarter', component: AddStatisticsComponent},
  {path: 'displayStats', component: DisplayStatsComponent}, 
  {path: 'displayStats/:gameId', component: DisplayStatsComponent}, 
  {path: '**', redirectTo: '', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
