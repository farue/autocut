import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { PaymentAccountDeleteDialogComponent } from 'app/entities/payment-account/payment-account-delete-dialog.component';
import { PaymentAccountService } from 'app/entities/payment-account/payment-account.service';

describe('Component Tests', () => {
  describe('PaymentAccount Management Delete Component', () => {
    let comp: PaymentAccountDeleteDialogComponent;
    let fixture: ComponentFixture<PaymentAccountDeleteDialogComponent>;
    let service: PaymentAccountService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentAccountDeleteDialogComponent]
      })
        .overrideTemplate(PaymentAccountDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PaymentAccountDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PaymentAccountService);
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
