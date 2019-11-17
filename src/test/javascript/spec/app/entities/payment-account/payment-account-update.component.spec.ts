import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { PaymentAccountUpdateComponent } from 'app/entities/payment-account/payment-account-update.component';
import { PaymentAccountService } from 'app/entities/payment-account/payment-account.service';
import { PaymentAccount } from 'app/shared/model/payment-account.model';

describe('Component Tests', () => {
  describe('PaymentAccount Management Update Component', () => {
    let comp: PaymentAccountUpdateComponent;
    let fixture: ComponentFixture<PaymentAccountUpdateComponent>;
    let service: PaymentAccountService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentAccountUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(PaymentAccountUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaymentAccountUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PaymentAccountService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new PaymentAccount(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new PaymentAccount();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
