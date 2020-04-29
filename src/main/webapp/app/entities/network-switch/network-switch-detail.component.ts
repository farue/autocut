import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';

@Component({
  selector: 'jhi-network-switch-detail',
  templateUrl: './network-switch-detail.component.html'
})
export class NetworkSwitchDetailComponent implements OnInit {
  networkSwitch: INetworkSwitch | null = null;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => (this.networkSwitch = networkSwitch));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }
}
