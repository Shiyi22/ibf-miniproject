import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  userId!: string

  constructor(private actRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.actRoute.params.subscribe((params) => {
      this.userId = params['userId']
    })
  }

}
