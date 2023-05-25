import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BlockDate, PlayerInfo } from '../models';
import { BackendService } from '../services/backend.service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {

  username: string = localStorage.getItem('username') as string
  selectedSection!: string;
  playerName!: string;
  identityForm!: FormGroup
  emergencyForm!: FormGroup
  teamForm!: FormGroup
  blockdates!: BlockDate[]
  playerInfo!: PlayerInfo

  constructor(private fb: FormBuilder, private backendSvc: BackendService) {}

  ngOnInit(): void {
    this.getPlayerInfo(); // will fill playerInfo property up 
  }

  getPlayerInfo() {
    // use username saved in localstorage to get id -> to get player info 
    this.backendSvc.getPlayerId(this.username).then(async (result:any) => {
      console.log('user id: ', result.userId)
      const userId = result.userId
      let r = await this.backendSvc.getPlayerInfo(userId); // r is a promise 
      this.playerInfo = r // assign promise r into PlayerInfo
      console.info('>>> Player Info sent from backend: ', this.playerInfo);

      this.identityForm = this.createIdentityForm(); 
      this.emergencyForm = this.createEmergencyForm(); 
      this.teamForm = this.createTeamForm(); 
    })
  }

  saveIdentity() {
    this.playerInfo.name = this.identityForm.value['name']
    this.playerInfo.weight = this.identityForm.value['weight']
    this.playerInfo.height = this.identityForm.value['height']
    this.playerInfo.email = this.identityForm.value['email']
    this.playerInfo.phoneNumber = this.identityForm.value['phoneNumber']
    this.playerInfo.weight = this.identityForm.value['weight']
    this.playerInfo.DOB = this.identityForm.value['DOB'] as string
    console.info('>>> print DOB ', this.playerInfo.DOB); // DOB is present here!
    console.info('>>> print name: ', this.playerInfo.name);

    const letters: string = this.identityForm.value['position'] as string // this is a string
    console.info('>>> letters from form in Angular: ', letters)
    console.info('>>> Positions in player info: ', this.playerInfo.positions) 
    const positions: string[] = letters.split(',') 
    console.info('>>> join n split by , ', positions); 
    this.playerInfo.positions = positions;

    // send data to backend to update
    console.info('>>> info to send to backend for UPDATE: ', this.playerInfo)
    this.backendSvc.updatePlayerInfo(this.playerInfo, this.username).then((results) => {
      console.info('Updated in backend: ', results)
    })
  }

  saveEForm() {
    this.playerInfo.emergencyContact = this.emergencyForm.value['eContact']
    this.playerInfo.emergencyName = this.emergencyForm.value['eName']
    this.playerInfo.address = this.emergencyForm.value['homeAdd']
    this.playerInfo.pastInjuries = this.emergencyForm.value['injuries']
    // send data to backend to update
    this.backendSvc.updatePlayerInfo(this.playerInfo, this.username).then((results) => {
      console.info('Updated in backend: ', results);
    })
  }

  showDetails(section: string): void {
    this.selectedSection = section;
  }

  createIdentityForm() {
    return this.fb.group({
      name: this.fb.control(this.playerInfo? this.playerInfo.name : '', [Validators.required, Validators.minLength(5)]),
      weight: this.fb.control(this.playerInfo? this.playerInfo.weight : ''), 
      height: this.fb.control(this.playerInfo? this.playerInfo.height : ''),
      position: this.fb.control(this.playerInfo? this.playerInfo.positions : ''),
      email: this.fb.control(this.playerInfo? this.playerInfo.email : ''),
      phoneNumber: this.fb.control(this.playerInfo? this.playerInfo.phoneNumber : ''),
      DOB: this.fb.control(this.playerInfo? this.playerInfo.DOB : '')
    })
  }

  createEmergencyForm() {
    return this.fb.group({
      eContact: this.fb.control(this.playerInfo? this.playerInfo.emergencyContact : ''),
      eName: this.fb.control(this.playerInfo? this.playerInfo.emergencyName : ''),
      homeAdd: this.fb.control(this.playerInfo? this.playerInfo.address : ''),
      injuries: this.fb.control(this.playerInfo? this.playerInfo.pastInjuries : '')
    })
  }

  createTeamForm() {
    return this.fb.group({
      role: this.fb.control(this.playerInfo? this.playerInfo.role : ''),
      year: this.fb.control(this.playerInfo? this.playerInfo.yearJoined : '')
    })
  }

}
