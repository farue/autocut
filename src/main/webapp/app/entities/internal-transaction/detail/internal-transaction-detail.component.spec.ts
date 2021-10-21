import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {InternalTransactionDetailComponent} from './internal-transaction-detail.component';

describe('Component Tests', () => {
  describe('InternalTransaction Management Detail Component', () => {
    let comp: InternalTransactionDetailComponent;
    let fixture: ComponentFixture<InternalTransactionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [InternalTransactionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ internalTransaction: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(InternalTransactionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InternalTransactionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load internalTransaction on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.internalTransaction).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
