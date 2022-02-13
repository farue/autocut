import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInternetAccess } from '../internet-access.model';
import { InternetAccessService } from '../service/internet-access.service';
import { InternetAccessDeleteDialogComponent } from '../delete/internet-access-delete-dialog.component';

@Component({
  selector: 'jhi-internet-access',
  templateUrl: './internet-access.component.html',
})
export class InternetAccessComponent implements OnInit {
  internetAccesses?: IInternetAccess[];
  isLoading = false;

  constructor(protected internetAccessService: InternetAccessService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.internetAccessService.query().subscribe({
      next: (res: HttpResponse<IInternetAccess[]>) => {
        this.isLoading = false;
        this.internetAccesses = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IInternetAccess): number {
    return item.id!;
  }

  delete(internetAccess: IInternetAccess): void {
    const modalRef = this.modalService.open(InternetAccessDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.internetAccess = internetAccess;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
