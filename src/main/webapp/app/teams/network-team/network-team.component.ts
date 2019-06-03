import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-network-team',
  templateUrl: './network-team.component.html',
  styleUrls: ['network-team.component.scss']
})
export class NetworkTeamComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'NetworkTeamComponent message';
  }

  ngOnInit() {}
}
