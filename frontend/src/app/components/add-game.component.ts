import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { GameData, PlayerProfile } from '../models';
import { BackendService } from '../services/backend.service';

@Component({
  selector: 'app-add-game',
  templateUrl: './add-game.component.html',
  styleUrls: ['./add-game.component.css']
})
export class AddGameComponent implements OnInit{
  
  @ViewChild(MatAccordion) accordion!: MatAccordion;
  
  gameForm!: FormGroup
  members!: PlayerProfile[]
  gameData!: GameData
  q1Form!: FormGroup

  constructor(private fb: FormBuilder, private backendSvc: BackendService) {}
  
  ngOnInit(): void {
    this.gameForm = this.createGameForm(); 
    this.backendSvc.getPlayerProfiles().then((result:any) => {
      this.members = result; 
      this.q1Form = this.createQ1Form(); 
    })
  }

  storeDescription() {
    // save in model
    this.gameData = this.gameForm.value as GameData
  }

  storeQ1Info() {
    // TODO: continue here ! 
  }

  createGameForm(): FormGroup {
    return this.fb.group({
      label: this.fb.control('', [Validators.required]),
      against: this.fb.control('', [Validators.required]),
      date: this.fb.control(new Date().toISOString().split('T')[0], [Validators.required])
    })
  }

  createQ1Form() {
    return this.fb.group({
      gs: this.fb.control('', [Validators.required]),
      ga: this.fb.control('', [Validators.required]),
      wa: this.fb.control('', [Validators.required]),
      c: this.fb.control('', [Validators.required]),
      wd: this.fb.control('', [Validators.required]),
      gd: this.fb.control('', [Validators.required]),
      gk: this.fb.control('', [Validators.required])
    })
  }

}
