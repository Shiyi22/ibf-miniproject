import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.css']
})
export class SuccessComponent implements OnInit {

  sessionId: string | null | undefined; 
  sessionDetails:any; 

  constructor(private actRoute: ActivatedRoute, private http: HttpClient) {}

  ngOnInit(): void {
    this.sessionId = this.actRoute.snapshot.queryParamMap.get('id')
    if (this.sessionId) {
      this.http.get(`/api/checkout-session?id=${this.sessionId}`).subscribe(details => {
        this.sessionDetails = details; 
      })
    }
  }

}
