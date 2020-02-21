import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-washing-team',
  templateUrl: './washing-team.component.html',
  styleUrls: ['washing-team.component.scss']
})
export class WashingTeamComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'WashTeamComponent message';
  }

  ngOnInit(): void {}
}
