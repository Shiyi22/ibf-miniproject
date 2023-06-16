import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  
  profileForm!: FormGroup

  constructor(private fb: FormBuilder) {}
  
  ngOnInit(): void {
    // create all the form to key in 
    this.initializeForm(); 
  }

  initializeForm() {
    return this.fb.group({
      name: this.fb.control('', [Validators.required]),
      weight: this.fb.control('', [Validators.required]),
      height: this.fb.control('', [Validators.required]),
      email: this.fb.control('', [Validators.required]),
      phoneNumber: this.fb.control('', [Validators.required]),
      DOB: this.fb.control('', [Validators.required]),

      eContact: this.fb.control('', [Validators.required]),
      eName: this.fb.control('', [Validators.required]),
      homeAdd: this.fb.control('', [Validators.required]),
      injuries: this.fb.control('', [Validators.required]),

      role: this.fb.control('', [Validators.required]),
      year: this.fb.control('', [Validators.required]),

      positions: this.fb.control('', [Validators.required]) // multiple select option
    })
  }

  save() {

    // change positions to string[] ? 

    // change photo into byte? 
  }

}
