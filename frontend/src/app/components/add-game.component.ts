import { Component, OnInit, ViewChild } from '@angular/core';
import { Form, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { GameData, PlayerProfile, QuarterData } from '../models';
import { BackendService } from '../services/backend.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-game',
  templateUrl: './add-game.component.html',
  styleUrls: ['./add-game.component.css']
})
export class AddGameComponent implements OnInit{
  
  @ViewChild(MatAccordion) accordion!: MatAccordion;
  
  gameForm!: FormGroup
  members!: PlayerProfile[]
  q1Form!: FormGroup
  q2Form!: FormGroup
  q3Form!: FormGroup
  q4Form!: FormGroup
  isFirstPanelExpanded = true; 
  isSecondPanelExpanded = false; 
  q1Ended: boolean = false; 
  q2Ended: boolean = false; 
  q3Ended: boolean = false; 

  constructor(private fb: FormBuilder, private backendSvc: BackendService, private router: Router) {}
  
  ngOnInit(): void {
    this.gameForm = this.createGameForm(); 
    this.backendSvc.getPlayerProfiles().then((result:any) => {
      this.members = result; 
      this.q1Form = this.createForm('1'); 

      // check if any quarter has ended
      if (this.backendSvc.q1Data != undefined) {
        this.q1Ended = true; 
        this.q2Form = this.createForm('2');
      }
      if (this.backendSvc.q2Data != undefined) {
        this.q2Ended = true; 
        this.q3Form = this.createForm('3'); 
      }
      if (this.backendSvc.q3Data != undefined) 
        this.q3Ended = true; 
        this.q4Form = this.createForm('4');
    })
  }

  storeDescription() {
    this.isFirstPanelExpanded = false;
    this.isSecondPanelExpanded = true;
    // save in model
    this.backendSvc.gameData = this.gameForm.value as GameData
    console.info('>>> Backend Game Data stored: ', this.backendSvc.gameData)
  }

  storeQ1Info() {
    this.backendSvc.q1Data = this.q1Form.value as QuarterData
    this.backendSvc.initVariables('1'); 
    console.info('>>> Backend Quarter 1 Data stored: ', this.backendSvc.q1Data)

    //navigate to buttons page 
    this.router.navigate(['/addStats', 1])
  }
  
  storeQ2Info() {
    this.backendSvc.q2Data = this.q2Form.value as QuarterData
    this.backendSvc.initVariables('2'); 
    console.info('>>> Backend Quarter 2 Data stored: ', this.backendSvc.q2Data)
    this.router.navigate(['/addStats', 2])
  }

  storeQ3Info() {
    this.backendSvc.q3Data = this.q3Form.value as QuarterData
    this.backendSvc.initVariables('3'); 
    console.info('>>> Backend Quarter 3 Data stored: ', this.backendSvc.q3Data)
    this.router.navigate(['/addStats', 3])
  }

  storeQ4Info() {
    this.backendSvc.q4Data = this.q4Form.value as QuarterData
    this.backendSvc.initVariables('4'); 
    console.info('>>> Backend Quarter 4 Data stored: ', this.backendSvc.q4Data)
    this.router.navigate(['/addStats', 4])
  }  

  createGameForm(): FormGroup {
    return this.fb.group({
      label: this.fb.control(this.backendSvc.gameData? this.backendSvc.gameData.label:'', [Validators.required]),
      against: this.fb.control(this.backendSvc.gameData? this.backendSvc.gameData.against:'', [Validators.required]),
      date: this.fb.control(this.backendSvc.gameData? this.backendSvc.gameData.date : new Date().toISOString().split('T')[0], [Validators.required])
    })
  }

  createForm(quarter: string) {
    const quarterData = this.backendSvc.getQuarterData(quarter); 
    return this.fb.group({
      duration: this.fb.control(quarterData? quarterData.duration : 15, [Validators.required, Validators.min(7), Validators.max(15)]),
      gs: this.fb.control(quarterData? quarterData.gs :'', [Validators.required]),
      ga: this.fb.control(quarterData? quarterData.ga :'', [Validators.required]),
      wa: this.fb.control(quarterData? quarterData.wa :'', [Validators.required]),
      c: this.fb.control(quarterData? quarterData.c :'', [Validators.required]),
      wd: this.fb.control(quarterData? quarterData.wd :'', [Validators.required]),
      gd: this.fb.control(quarterData? quarterData.gd :'', [Validators.required]),
      gk: this.fb.control(quarterData? quarterData.gk :'', [Validators.required])
    })
  }

}
