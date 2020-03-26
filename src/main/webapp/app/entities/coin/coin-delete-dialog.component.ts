import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICoin } from 'app/shared/model/coin.model';
import { CoinService } from './coin.service';

@Component({
  templateUrl: './coin-delete-dialog.component.html'
})
export class CoinDeleteDialogComponent {
  coin?: ICoin;

  constructor(protected coinService: CoinService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.coinService.delete(id).subscribe(() => {
      this.eventManager.broadcast('coinListModification');
      this.activeModal.close();
    });
  }
}
