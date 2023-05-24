import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  isLoggedIn: boolean = false; 
  checked: boolean = false; 

  constructor() { }

  checkLogin() {
    const storage = localStorage.getItem('jwt');
    if (storage == null)
      return; 
    console.log('>>> jwt retrieved: ', storage); 
    this.isLoggedIn = true; 
    this.checked = true;
  }

}
