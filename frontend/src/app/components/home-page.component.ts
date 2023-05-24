import { Component, OnInit } from '@angular/core';
import { LoginService } from '../services/login.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  isLoggedIn: boolean = false; 

  constructor(private loginSvc: LoginService) {}
  
  ngOnInit(): void {
    if (!this.loginSvc.checked) 
      this.loginSvc.checkLogin()

    if (this.loginSvc.isLoggedIn)
      this.isLoggedIn = true; 
  }

  
}
