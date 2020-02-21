import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-networking-team',
  templateUrl: './networking-team.component.html',
  styleUrls: ['networking-team.component.scss']
})
export class NetworkingTeamComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'NetworkTeamComponent message';
  }

  ngOnInit(): void {}
}
