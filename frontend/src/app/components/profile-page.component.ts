import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BlockDate, PlayerInfo } from '../models';
import { BackendService } from '../services/backend.service';
import { lastValueFrom } from 'rxjs';
import { LoginService } from '../services/login.service';

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
  photoForm!: FormGroup
  blockdates!: BlockDate[]
  playerInfo!: PlayerInfo

  @ViewChild('picture') playerPhoto!: ElementRef;

  constructor(private fb: FormBuilder, private backendSvc: BackendService, private loginSvc: LoginService) {}

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
      // this.teamForm = this.createTeamForm(); 
      this.photoForm = this.createPhotoForm(); 
    })
  }

  saveIdentity() {
    this.playerInfo.name = this.identityForm.value['name']
    this.playerInfo.weight = this.identityForm.value['weight']
    this.playerInfo.height = this.identityForm.value['height']
    this.playerInfo.email = this.identityForm.value['email']
    this.playerInfo.phoneNumber = this.identityForm.value['phoneNumber']
    this.playerInfo.weight = this.identityForm.value['weight']
    const date: string = this.identityForm.get('DOB')?.value
    console.log('>>> print date value from form: ', date);
    this.playerInfo.dob = date
    console.info('>>> print DOB ', this.playerInfo.dob); // DOB is present here as string

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

  updatePhoto() {
    const formData = new FormData(); 
    formData.set("picture", this.playerPhoto.nativeElement.files[0])
    this.backendSvc.updatePhoto(formData, this.username)
  }

  showDetails(section: string): void {
    this.selectedSection = section;
  }

  createIdentityForm() {  
    const combinedString = this.playerInfo.positions.join(",");  
    return this.fb.group({
      name: this.fb.control(this.playerInfo? this.playerInfo.name : '', [Validators.required, Validators.minLength(5)]),
      weight: this.fb.control(this.playerInfo? this.playerInfo.weight : ''), 
      height: this.fb.control(this.playerInfo? this.playerInfo.height : ''),
      position: this.fb.control(this.playerInfo? combinedString : ''),
      email: this.fb.control(this.playerInfo? this.playerInfo.email : ''),
      phoneNumber: this.fb.control(this.playerInfo? this.playerInfo.phoneNumber : ''),
      DOB: this.fb.control(this.playerInfo? this.playerInfo.dob : '')
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

  // createTeamForm() {
  //   return this.fb.group({
  //     role: this.fb.control(this.playerInfo? this.playerInfo.role : ''),
  //     year: this.fb.control(this.playerInfo? this.playerInfo.yearJoined : '')
  //   })
  // }

  createPhotoForm() {
    return this.fb.group({
      playerPhoto: this.fb.control('')
    })
  }

  logout() {
    localStorage.removeItem('username')
    localStorage.removeItem('jwt')
    this.loginSvc.isLoggedIn = false; 
  }

}
