import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IApartment } from '../apartment.model';
import { ApartmentService } from '../service/apartment.service';
import { ApartmentDeleteDialogComponent } from '../delete/apartment-delete-dialog.component';

@Component({
  selector: 'jhi-apartment',
  templateUrl: './apartment.component.html',
})
export class ApartmentComponent implements OnInit {
  apartments?: IApartment[];
  isLoading = false;

  constructor(protected apartmentService: ApartmentService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.apartmentService.query().subscribe({
      next: (res: HttpResponse<IApartment[]>) => {
        this.isLoading = false;
        this.apartments = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IApartment): number {
    return item.id!;
  }

  delete(apartment: IApartment): void {
    const modalRef = this.modalService.open(ApartmentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.apartment = apartment;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
