import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INetworkSwitch } from '../network-switch.model';
import { NetworkSwitchService } from '../service/network-switch.service';
import { NetworkSwitchDeleteDialogComponent } from '../delete/network-switch-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-network-switch',
  templateUrl: './network-switch.component.html',
})
export class NetworkSwitchComponent implements OnInit {
  networkSwitches?: INetworkSwitch[];
  isLoading = false;

  constructor(protected networkSwitchService: NetworkSwitchService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.networkSwitchService.query().subscribe(
      (res: HttpResponse<INetworkSwitch[]>) => {
        this.isLoading = false;
        this.networkSwitches = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: INetworkSwitch): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(networkSwitch: INetworkSwitch): void {
    const modalRef = this.modalService.open(NetworkSwitchDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.networkSwitch = networkSwitch;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
