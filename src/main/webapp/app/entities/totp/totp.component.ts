import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITotp } from 'app/shared/model/totp.model';
import { TotpService } from './totp.service';
import { TotpDeleteDialogComponent } from './totp-delete-dialog.component';

@Component({
  selector: 'jhi-totp',
  templateUrl: './totp.component.html'
})
export class TotpComponent implements OnInit, OnDestroy {
  totps?: ITotp[];
  eventSubscriber?: Subscription;

  constructor(protected totpService: TotpService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.totpService.query().subscribe((res: HttpResponse<ITotp[]>) => {
      this.totps = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTotps();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITotp): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTotps(): void {
    this.eventSubscriber = this.eventManager.subscribe('totpListModification', () => this.loadAll());
  }

  delete(totp: ITotp): void {
    const modalRef = this.modalService.open(TotpDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.totp = totp;
  }
}
