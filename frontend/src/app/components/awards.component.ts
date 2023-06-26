import { Component, OnInit } from '@angular/core';
import { AnimationOptions } from 'ngx-lottie';

@Component({
  selector: 'app-awards',
  templateUrl: './awards.component.html',
  styleUrls: ['./awards.component.css']
})
export class AwardsComponent implements OnInit {
  
  options!: AnimationOptions

  ngOnInit(): void {
    this.options = {
      path: '../../assets/107653-trophy.json'
    }
  }

}
