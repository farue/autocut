import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { ApartmentDeleteDialogComponent } from 'app/entities/apartment/apartment-delete-dialog.component';
import { ApartmentService } from 'app/entities/apartment/apartment.service';

describe('Component Tests', () => {
  describe('Apartment Management Delete Component', () => {
    let comp: ApartmentDeleteDialogComponent;
    let fixture: ComponentFixture<ApartmentDeleteDialogComponent>;
    let service: ApartmentService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [ApartmentDeleteDialogComponent]
      })
        .overrideTemplate(ApartmentDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ApartmentDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ApartmentService);
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
