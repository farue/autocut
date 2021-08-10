jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { BroadcastMessageTextService } from '../service/broadcast-message-text.service';

import { BroadcastMessageTextDeleteDialogComponent } from './broadcast-message-text-delete-dialog.component';

describe('Component Tests', () => {
  describe('BroadcastMessageText Management Delete Component', () => {
    let comp: BroadcastMessageTextDeleteDialogComponent;
    let fixture: ComponentFixture<BroadcastMessageTextDeleteDialogComponent>;
    let service: BroadcastMessageTextService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BroadcastMessageTextDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(BroadcastMessageTextDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BroadcastMessageTextDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BroadcastMessageTextService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

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
        spyOn(service, 'delete');

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
