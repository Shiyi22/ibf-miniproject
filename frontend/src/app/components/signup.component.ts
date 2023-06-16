import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PlayerInfo } from '../models';
import { BackendService } from '../services/backend.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  
  profileForm!: FormGroup
  
  @ViewChild('picture') playerPhoto!: ElementRef;

  constructor(private fb: FormBuilder, private backendSvc: BackendService, private router: Router) {}
  
  ngOnInit(): void {
    // create all the form to key in 
    this.profileForm = this.initializeForm(); 
  }

  initializeForm() {
    return this.fb.group({
      name: this.fb.control('', [Validators.required]),
      weight: this.fb.control('', [Validators.required]),
      height: this.fb.control('', [Validators.required]),
      email: this.fb.control('', [Validators.required]),
      phoneNumber: this.fb.control('', [Validators.required]),
      DOB: this.fb.control(''),

      eContact: this.fb.control('', [Validators.required]),
      eName: this.fb.control('', [Validators.required]),
      homeAdd: this.fb.control('', [Validators.required]),
      injuries: this.fb.control('', [Validators.required]),

      role: this.fb.control('', [Validators.required]),
      year: this.fb.control('', [Validators.required]),

      positions: this.fb.control(''), // multiple select option

      playerPhoto: this.fb.control('')
    })
  }

  save() {
    const formData = new FormData();
    formData.append('name', this.profileForm.value.name)
    formData.append('weight', this.profileForm.value.weight)
    formData.append('height', this.profileForm.value.height)
    formData.append('email', this.profileForm.value.email)
    formData.append('phoneNumber', this.profileForm.value.phoneNumber)
    formData.append('dob', this.profileForm.value.DOB)
    formData.append('emergencyContact', this.profileForm.value.eContact)
    formData.append('emergencyName', this.profileForm.value.eName)
    formData.append('address', this.profileForm.value.homeAdd)
    formData.append('pastInjuries', this.profileForm.value.injuries)
    formData.append('role', this.profileForm.value.role)
    formData.append('yearJoined', this.profileForm.value.year)
    formData.append('positions', this.profileForm.value.positions)
    formData.append('playerPhoto', this.playerPhoto.nativeElement.files[0])
    

    const username = localStorage.getItem('username')!
    formData.append('username', username)
    this.backendSvc.savePlayerInfo(formData).then((result:any) => {
        console.info('>>> Profile saved to database: ', result.isSaved)
        this.router.navigate(['/profile'])
    })

  }

}
