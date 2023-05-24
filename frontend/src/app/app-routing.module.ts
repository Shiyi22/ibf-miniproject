import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { HomePageComponent } from './components/home-page.component';
import { LoginPageComponent } from './components/login-page.component';
import { NotificationsComponent } from './components/notifications.component';
import { ProfilePageComponent } from './components/profile-page.component';

const routes: Routes = [
  {path: '', component: HomePageComponent},
  {path: 'login', component: LoginPageComponent},
  {path: 'notifications', component: NotificationsComponent},
  {path: 'profile', component: ProfilePageComponent},
  {path: '**', redirectTo: '', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
