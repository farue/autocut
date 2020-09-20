import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { TransactionBookComponent } from 'app/entities/transaction-book/transaction-book.component';
import { TransactionBookService } from 'app/entities/transaction-book/transaction-book.service';
import { TransactionBook } from 'app/shared/model/transaction-book.model';

describe('Component Tests', () => {
  describe('TransactionBook Management Component', () => {
    let comp: TransactionBookComponent;
    let fixture: ComponentFixture<TransactionBookComponent>;
    let service: TransactionBookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TransactionBookComponent],
      })
        .overrideTemplate(TransactionBookComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TransactionBookComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TransactionBookService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new TransactionBook(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.transactionBooks && comp.transactionBooks[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
