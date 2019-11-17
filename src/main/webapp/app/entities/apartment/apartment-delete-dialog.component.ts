import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from './apartment.service';

@Component({
  selector: 'jhi-apartment-delete-dialog',
  templateUrl: './apartment-delete-dialog.component.html'
})
export class ApartmentDeleteDialogComponent {
  apartment: IApartment;

  constructor(protected apartmentService: ApartmentService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.apartmentService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'apartmentListModification',
        content: 'Deleted an apartment'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-apartment-delete-popup',
  template: ''
})
export class ApartmentDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ apartment }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ApartmentDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.apartment = apartment;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/apartment', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/apartment', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
