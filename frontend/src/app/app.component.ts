import { Component } from '@angular/core';
import { LoginService } from './services/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';

  constructor(private loginSvc: LoginService, private router: Router) {}

  checkLoggedIn(direction: string) {
    if (!this.loginSvc.checked) 
      this.loginSvc.checkLogin()

    console.info('>>> are we logged in? ', this.loginSvc.isLoggedIn)
    console.info('>>> direction: ', direction)
    if (this.loginSvc.isLoggedIn) {
      this.router.navigate(['/', direction]) // go to notif page 
      return
    }
    this.router.navigate(['/login']) // else go to login page 
  }
}
