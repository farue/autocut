import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { BankTransactionComponent } from 'app/entities/bank-transaction/bank-transaction.component';
import { BankTransactionService } from 'app/entities/bank-transaction/bank-transaction.service';
import { BankTransaction } from 'app/shared/model/bank-transaction.model';

describe('Component Tests', () => {
  describe('BankTransaction Management Component', () => {
    let comp: BankTransactionComponent;
    let fixture: ComponentFixture<BankTransactionComponent>;
    let service: BankTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [BankTransactionComponent],
      })
        .overrideTemplate(BankTransactionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankTransactionComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BankTransactionService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new BankTransaction(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.bankTransactions && comp.bankTransactions[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
