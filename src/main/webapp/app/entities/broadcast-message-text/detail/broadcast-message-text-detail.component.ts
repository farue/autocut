import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBroadcastMessageText } from '../broadcast-message-text.model';

@Component({
  selector: 'jhi-broadcast-message-text-detail',
  templateUrl: './broadcast-message-text-detail.component.html',
})
export class BroadcastMessageTextDetailComponent implements OnInit {
  broadcastMessageText: IBroadcastMessageText | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ broadcastMessageText }) => {
      this.broadcastMessageText = broadcastMessageText;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
