import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BlockDate } from '../models';
import { BackendService } from '../services/backend.service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {

  selectedSection!: string;
  playerName!: string;
  identityForm!: FormGroup
  emergencyForm!: FormGroup
  teamForm!: FormGroup
  blockdates!: BlockDate[]

  constructor(private fb: FormBuilder, private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.getPlayerInfo();
    this.identityForm = this.createIdentityForm(); 
    this.emergencyForm = this.createEmergencyForm(); 
    this.teamForm = this.createTeamForm(); 
  }

  getPlayerInfo() {
    // use username saved in localstorage to get id -> to get player info 
    const username = localStorage.getItem('username') as string
    this.backendSvc.getPlayerId(username).then((result:any) => {
      console.log('user id: ', result.userId)
      const userId = result.userId
      this.backendSvc.getPlayerInfo(userId).then((results) => { // entire json string of player info
        console.info('results: ', results)
        // map results to playerInfo model (create)
        
      })  
    })
  }

  showDetails(section: string): void {
    this.selectedSection = section;
  }

  createIdentityForm() {
    return this.fb.group({
      name: this.fb.control('', [Validators.required, Validators.minLength(5)]),
      weight: this.fb.control(''), 
      height: this.fb.control(''),
      position: this.fb.control(''),
      email: this.fb.control(''),
      phoneNumber: this.fb.control(''),
      DOB: this.fb.control('')
    })
  }

  createEmergencyForm() {
    return this.fb.group({
      eContact: this.fb.control(''),
      eName: this.fb.control(''),
      homeAdd: this.fb.control(''),
      injuries: this.fb.control('')
    })
  }

  createTeamForm() {
    return this.fb.group({
      role: this.fb.control(''),
      year: this.fb.control('')
    })
  }

}
