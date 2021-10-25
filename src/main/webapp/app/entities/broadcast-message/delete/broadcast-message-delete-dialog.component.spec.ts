jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { BroadcastMessageService } from '../service/broadcast-message.service';

import { BroadcastMessageDeleteDialogComponent } from './broadcast-message-delete-dialog.component';

describe('Component Tests', () => {
  describe('BroadcastMessage Management Delete Component', () => {
    let comp: BroadcastMessageDeleteDialogComponent;
    let fixture: ComponentFixture<BroadcastMessageDeleteDialogComponent>;
    let service: BroadcastMessageService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BroadcastMessageDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(BroadcastMessageDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BroadcastMessageDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BroadcastMessageService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({})));

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
});
