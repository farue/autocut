import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TransactionBookDetailComponent } from 'app/entities/transaction-book/transaction-book-detail.component';
import { TransactionBook } from 'app/shared/model/transaction-book.model';

describe('Component Tests', () => {
  describe('TransactionBook Management Detail Component', () => {
    let comp: TransactionBookDetailComponent;
    let fixture: ComponentFixture<TransactionBookDetailComponent>;
    const route = ({ data: of({ transactionBook: new TransactionBook(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TransactionBookDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(TransactionBookDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TransactionBookDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load transactionBook on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.transactionBook).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
