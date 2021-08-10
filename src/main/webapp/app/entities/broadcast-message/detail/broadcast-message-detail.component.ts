import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBroadcastMessage } from '../broadcast-message.model';

@Component({
  selector: 'jhi-broadcast-message-detail',
  templateUrl: './broadcast-message-detail.component.html',
})
export class BroadcastMessageDetailComponent implements OnInit {
  broadcastMessage: IBroadcastMessage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ broadcastMessage }) => {
      this.broadcastMessage = broadcastMessage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
