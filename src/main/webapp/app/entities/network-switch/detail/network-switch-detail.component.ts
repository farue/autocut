import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INetworkSwitch } from '../network-switch.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-network-switch-detail',
  templateUrl: './network-switch-detail.component.html',
})
export class NetworkSwitchDetailComponent implements OnInit {
  networkSwitch: INetworkSwitch | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      this.networkSwitch = networkSwitch;
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
