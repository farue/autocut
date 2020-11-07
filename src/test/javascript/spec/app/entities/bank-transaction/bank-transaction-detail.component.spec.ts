import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { BankTransactionDetailComponent } from 'app/entities/bank-transaction/bank-transaction-detail.component';
import { BankTransaction } from 'app/shared/model/bank-transaction.model';

describe('Component Tests', () => {
  describe('BankTransaction Management Detail Component', () => {
    let comp: BankTransactionDetailComponent;
    let fixture: ComponentFixture<BankTransactionDetailComponent>;
    const route = ({ data: of({ bankTransaction: new BankTransaction(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [BankTransactionDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(BankTransactionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BankTransactionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load bankTransaction on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.bankTransaction).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
