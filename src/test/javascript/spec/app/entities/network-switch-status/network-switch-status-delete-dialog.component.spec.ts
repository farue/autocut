import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';
import { NetworkSwitchStatusDeleteDialogComponent } from 'app/entities/network-switch-status/network-switch-status-delete-dialog.component';
import { NetworkSwitchStatusService } from 'app/entities/network-switch-status/network-switch-status.service';

describe('Component Tests', () => {
  describe('NetworkSwitchStatus Management Delete Component', () => {
    let comp: NetworkSwitchStatusDeleteDialogComponent;
    let fixture: ComponentFixture<NetworkSwitchStatusDeleteDialogComponent>;
    let service: NetworkSwitchStatusService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchStatusDeleteDialogComponent],
      })
        .overrideTemplate(NetworkSwitchStatusDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(NetworkSwitchStatusDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NetworkSwitchStatusService);
      mockEventManager = TestBed.get(JhiEventManager);
      mockActiveModal = TestBed.get(NgbActiveModal);
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
          expect(mockActiveModal.closeSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      });
    });
  });
});
