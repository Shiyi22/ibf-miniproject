import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Login } from '../models';
import { lastValueFrom } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  form!: FormGroup
  errorMsg!: string

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.form = this.createForm()
  }
  
  createForm() {
    return this.fb.group({
      username: this.fb.control('', [Validators.required, Validators.minLength(5)]),
      password: this.fb.control('', [Validators.required, Validators.minLength(8)])
    })
  }

  // send login details to backend as json
  login() {
    const login = this.form.value as Login
    lastValueFrom (this.http.post('/login', login)).then((results: any) => {
      console.info('>>> login returned results: ', results)
      
      const message: string = results.message
      if (message.includes('Invalid')) {
        // if fail -> stay on login page and display error message 
        this.errorMsg = message;
      }
      // else login is successful, store token in local storage for future API requests
      localStorage.setItem('jwt', message);
      localStorage.setItem('username', login.username); 
      // redirect user to desired page (full)
      this.router.navigate(['/'])
      
    }).catch((err) => console.log(err))
    
  }

}
