import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { Login, Signup } from '../models';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  isLoggedIn: boolean = false; 
  checked: boolean = false; 

  constructor(private http: HttpClient) { }

  getLoginCreds(login: Login) {
    return lastValueFrom (this.http.post('https://net-pro-production.up.railway.app/login', login))
  }

  checkLogin() {
    const storage = localStorage.getItem('jwt');
    if (storage == null)
      return; 
    // console.log('>>> jwt retrieved: ', storage); 
    this.isLoggedIn = true; 
    this.checked = true;
  }

  addEmailToMemberList(email: string) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/addEmailToList', email))
  }

  getEmailListApproved() {
    return lastValueFrom(this.http.get('https://net-pro-production.up.railway.app/getApprovedEmailList'))
  }

  // save login creds
  saveLoginCreds(signup: Signup) {
    return lastValueFrom(this.http.post('https://net-pro-production.up.railway.app/saveLoginCreds', signup))
  }

}
