import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-assignment-committee',
  templateUrl: './assignment-committee.component.html',
  styleUrls: ['assignment-committee.component.scss']
})
export class AssignmentCommitteeComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'AssignmentCommitteeComponent message';
  }

  ngOnInit() {}
}
