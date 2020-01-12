import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-wash-team',
  templateUrl: './wash-team.component.html',
  styleUrls: ['wash-team.component.scss']
})
export class WashTeamComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'WashTeamComponent message';
  }

  ngOnInit(): void {}
}
