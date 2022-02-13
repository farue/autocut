jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { NetworkSwitchStatusService } from '../service/network-switch-status.service';

import { NetworkSwitchStatusDeleteDialogComponent } from './network-switch-status-delete-dialog.component';

describe('NetworkSwitchStatus Management Delete Component', () => {
  let comp: NetworkSwitchStatusDeleteDialogComponent;
  let fixture: ComponentFixture<NetworkSwitchStatusDeleteDialogComponent>;
  let service: NetworkSwitchStatusService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [NetworkSwitchStatusDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(NetworkSwitchStatusDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NetworkSwitchStatusDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NetworkSwitchStatusService);
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
