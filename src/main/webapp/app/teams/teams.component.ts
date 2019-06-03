import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['teams.component.scss']
})
export class TeamsComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'TeamsComponent message';
  }

  ngOnInit() {}
}
