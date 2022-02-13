import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INetworkSwitchStatus } from '../network-switch-status.model';
import { NetworkSwitchStatusService } from '../service/network-switch-status.service';
import { NetworkSwitchStatusDeleteDialogComponent } from '../delete/network-switch-status-delete-dialog.component';

@Component({
  selector: 'jhi-network-switch-status',
  templateUrl: './network-switch-status.component.html',
})
export class NetworkSwitchStatusComponent implements OnInit {
  networkSwitchStatuses?: INetworkSwitchStatus[];
  isLoading = false;

  constructor(protected networkSwitchStatusService: NetworkSwitchStatusService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.networkSwitchStatusService.query().subscribe({
      next: (res: HttpResponse<INetworkSwitchStatus[]>) => {
        this.isLoading = false;
        this.networkSwitchStatuses = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: INetworkSwitchStatus): number {
    return item.id!;
  }

  delete(networkSwitchStatus: INetworkSwitchStatus): void {
    const modalRef = this.modalService.open(NetworkSwitchStatusDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.networkSwitchStatus = networkSwitchStatus;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
