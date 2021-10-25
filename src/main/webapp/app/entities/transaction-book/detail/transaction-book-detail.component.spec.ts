import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TransactionBookDetailComponent } from './transaction-book-detail.component';

describe('TransactionBook Management Detail Component', () => {
  let comp: TransactionBookDetailComponent;
  let fixture: ComponentFixture<TransactionBookDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransactionBookDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ transactionBook: { id: 123 } }) },
        },
      ],
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
      expect(comp.transactionBook).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
