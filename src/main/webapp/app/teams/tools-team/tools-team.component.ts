import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-tools-team',
  templateUrl: './tools-team.component.html',
  styleUrls: ['tools-team.component.scss']
})
export class ToolsTeamComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'ToolsTeamComponent message';
  }

  ngOnInit() {}
}
