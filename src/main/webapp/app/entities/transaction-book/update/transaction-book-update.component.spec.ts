jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TransactionBookService } from '../service/transaction-book.service';
import { ITransactionBook, TransactionBook } from '../transaction-book.model';

import { TransactionBookUpdateComponent } from './transaction-book-update.component';

describe('Component Tests', () => {
  describe('TransactionBook Management Update Component', () => {
    let comp: TransactionBookUpdateComponent;
    let fixture: ComponentFixture<TransactionBookUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let transactionBookService: TransactionBookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TransactionBookUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TransactionBookUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TransactionBookUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      transactionBookService = TestBed.inject(TransactionBookService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const transactionBook: ITransactionBook = { id: 456 };

        activatedRoute.data = of({ transactionBook });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(transactionBook));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const transactionBook = { id: 123 };
        spyOn(transactionBookService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ transactionBook });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: transactionBook }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(transactionBookService.update).toHaveBeenCalledWith(transactionBook);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const transactionBook = new TransactionBook();
        spyOn(transactionBookService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ transactionBook });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: transactionBook }));
        saveSubject.complete();

        // THEN
        expect(transactionBookService.create).toHaveBeenCalledWith(transactionBook);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const transactionBook = { id: 123 };
        spyOn(transactionBookService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ transactionBook });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(transactionBookService.update).toHaveBeenCalledWith(transactionBook);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
