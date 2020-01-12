import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { PaymentEntryUpdateComponent } from 'app/entities/payment-entry/payment-entry-update.component';
import { PaymentEntryService } from 'app/entities/payment-entry/payment-entry.service';
import { PaymentEntry } from 'app/shared/model/payment-entry.model';

describe('Component Tests', () => {
  describe('PaymentEntry Management Update Component', () => {
    let comp: PaymentEntryUpdateComponent;
    let fixture: ComponentFixture<PaymentEntryUpdateComponent>;
    let service: PaymentEntryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentEntryUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(PaymentEntryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaymentEntryUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PaymentEntryService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new PaymentEntry(123);
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
        const entity = new PaymentEntry();
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
