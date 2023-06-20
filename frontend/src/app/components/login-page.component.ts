import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EmailRequest, Login, Signup } from '../models';
import { lastValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { Backend2Service } from '../services/backend-2.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LoginService } from '../services/login.service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  form!: FormGroup
  verification!: FormGroup
  signupForm!: FormGroup
  errorMsg!: string
  signupActivated: boolean = false 
  setupActivated: boolean = false; 
  emailApproved!: string[]
  verified: boolean = false; 
  verificationEnded: boolean = false; 

  constructor(private fb: FormBuilder, private router: Router, 
                private loginSvc: LoginService, private snackBar: MatSnackBar, private backend2Svc: Backend2Service) {}

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
    this.loginSvc.getLoginCreds(login).then((results: any) => {
      console.info('>>> login returned results: ', results)
      
      const message: string = results.message
      if (message.includes('Invalid')) {
        // if fail -> stay on login page and display error message 
        this.errorMsg = message;
      } else {
        // else login is successful, store token in local storage for future API requests
        localStorage.setItem('jwt', message);
        localStorage.setItem('username', login.username); 
        // redirect user to desired page (full)
        this.router.navigate(['/'])
      }
    }).catch((err) => console.log(err))
    
  }

  signup() {
    this.signupActivated = true; 
    this.verification = this.fb.group({
      email: this.fb.control('', [Validators.required]) 
    })
  }

  sendReqToCaptain() {
    // store email address 
    const email = this.verification.value['email']
    // fire email to captain 
    const emailReq: EmailRequest = {to: ['teamkryptonite2008@gmail.com'], subject: 'New user signup request', 
                                    body: 'Notification: A new user with email: ' + email + ' requested to be added as a member. Please add new user email on app and reply to the email if approved.'}

    this.backend2Svc.sendEmailUsingSB(emailReq).then((result:any) => {
      console.info('>>> Email sent: ', result.emailSent)
      if (result.emailSent == true) {
        this.snackBar.open('Request successful, please check your email for approval from teamkryptonite2008@gmail.com', 'Dismiss', {duration: 5000})
      }
    })
  }

  setup() {
    this.setupActivated = true; 
    // get the email list 
    this.loginSvc.getEmailListApproved().then((result:any) => {
      this.emailApproved = result
    })

    this.signupForm = this.fb.group({
      email: this.fb.control('', [Validators.required]),
      username: this.fb.control('', [Validators.required, Validators.minLength(5)]),
      password: this.fb.control('', [Validators.required, Validators.minLength(8)])
    })
  }

  createAccount() {
    const signup = this.signupForm.value as Signup
    this.emailApproved.forEach((email) => {
      if (signup.email == email)
        this.verified = true; 
    })
    this.verificationEnded = true; 
    if (this.verified == true) {
      // save login creds to database 
      this.loginSvc.saveLoginCreds(signup).then((result:any) => {
        console.info('>>> Login creds saved: ', result.isSaved);

        // get generated jwt
        const login = {username: signup.username, password: signup.password}
        this.loginSvc.getLoginCreds(login).then((result:any) => {
          // store in localStorage
          localStorage.setItem('jwt', result.message);
          localStorage.setItem('username', signup.username); 

          // direct them to profile set up
          this.router.navigate(['/signup'])    
        })
      })
      
    }
  }



}
