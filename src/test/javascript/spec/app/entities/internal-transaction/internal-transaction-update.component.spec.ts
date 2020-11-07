import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { InternalTransactionUpdateComponent } from 'app/entities/internal-transaction/internal-transaction-update.component';
import { InternalTransactionService } from 'app/entities/internal-transaction/internal-transaction.service';
import { InternalTransaction } from 'app/shared/model/internal-transaction.model';

describe('Component Tests', () => {
  describe('InternalTransaction Management Update Component', () => {
    let comp: InternalTransactionUpdateComponent;
    let fixture: ComponentFixture<InternalTransactionUpdateComponent>;
    let service: InternalTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternalTransactionUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(InternalTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternalTransactionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InternalTransactionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new InternalTransaction(123);
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
        const entity = new InternalTransaction();
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
