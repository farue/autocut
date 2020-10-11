import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TransactionBookUpdateComponent } from 'app/entities/transaction-book/transaction-book-update.component';
import { TransactionBookService } from 'app/entities/transaction-book/transaction-book.service';
import { TransactionBook } from 'app/shared/model/transaction-book.model';

describe('Component Tests', () => {
  describe('TransactionBook Management Update Component', () => {
    let comp: TransactionBookUpdateComponent;
    let fixture: ComponentFixture<TransactionBookUpdateComponent>;
    let service: TransactionBookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TransactionBookUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(TransactionBookUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TransactionBookUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TransactionBookService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new TransactionBook(123);
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
        const entity = new TransactionBook();
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
