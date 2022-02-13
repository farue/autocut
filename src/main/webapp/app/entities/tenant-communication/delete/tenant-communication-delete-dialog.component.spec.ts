jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { TenantCommunicationService } from '../service/tenant-communication.service';

import { TenantCommunicationDeleteDialogComponent } from './tenant-communication-delete-dialog.component';

describe('TenantCommunication Management Delete Component', () => {
  let comp: TenantCommunicationDeleteDialogComponent;
  let fixture: ComponentFixture<TenantCommunicationDeleteDialogComponent>;
  let service: TenantCommunicationService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TenantCommunicationDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(TenantCommunicationDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TenantCommunicationDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TenantCommunicationService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
