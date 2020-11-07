import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { BankTransactionUpdateComponent } from 'app/entities/bank-transaction/bank-transaction-update.component';
import { BankTransactionService } from 'app/entities/bank-transaction/bank-transaction.service';
import { BankTransaction } from 'app/shared/model/bank-transaction.model';

describe('Component Tests', () => {
  describe('BankTransaction Management Update Component', () => {
    let comp: BankTransactionUpdateComponent;
    let fixture: ComponentFixture<BankTransactionUpdateComponent>;
    let service: BankTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [BankTransactionUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(BankTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankTransactionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BankTransactionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new BankTransaction(123);
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
        const entity = new BankTransaction();
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
