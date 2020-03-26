import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';
import { LaundryMachineProgramDeleteDialogComponent } from 'app/entities/laundry-machine-program/laundry-machine-program-delete-dialog.component';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/laundry-machine-program.service';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Delete Component', () => {
    let comp: LaundryMachineProgramDeleteDialogComponent;
    let fixture: ComponentFixture<LaundryMachineProgramDeleteDialogComponent>;
    let service: LaundryMachineProgramService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineProgramDeleteDialogComponent]
      })
        .overrideTemplate(LaundryMachineProgramDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LaundryMachineProgramDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LaundryMachineProgramService);
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
        comp.clear();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      });
    });
  });
});
