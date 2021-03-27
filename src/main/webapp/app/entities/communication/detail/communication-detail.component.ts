import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICommunication } from '../communication.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-communication-detail',
  templateUrl: './communication-detail.component.html',
})
export class CommunicationDetailComponent implements OnInit {
  communication: ICommunication | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ communication }) => {
      this.communication = communication;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
