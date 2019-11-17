import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { TenantCommunicationDeleteDialogComponent } from 'app/entities/tenant-communication/tenant-communication-delete-dialog.component';
import { TenantCommunicationService } from 'app/entities/tenant-communication/tenant-communication.service';

describe('Component Tests', () => {
  describe('TenantCommunication Management Delete Component', () => {
    let comp: TenantCommunicationDeleteDialogComponent;
    let fixture: ComponentFixture<TenantCommunicationDeleteDialogComponent>;
    let service: TenantCommunicationService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantCommunicationDeleteDialogComponent]
      })
        .overrideTemplate(TenantCommunicationDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TenantCommunicationDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TenantCommunicationService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
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
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
