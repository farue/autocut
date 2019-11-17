import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { InternetAccessDeleteDialogComponent } from 'app/entities/internet-access/internet-access-delete-dialog.component';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';

describe('Component Tests', () => {
  describe('InternetAccess Management Delete Component', () => {
    let comp: InternetAccessDeleteDialogComponent;
    let fixture: ComponentFixture<InternetAccessDeleteDialogComponent>;
    let service: InternetAccessService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternetAccessDeleteDialogComponent]
      })
        .overrideTemplate(InternetAccessDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InternetAccessDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InternetAccessService);
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
