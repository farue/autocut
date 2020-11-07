import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { InternalTransactionDetailComponent } from 'app/entities/internal-transaction/internal-transaction-detail.component';
import { InternalTransaction } from 'app/shared/model/internal-transaction.model';

describe('Component Tests', () => {
  describe('InternalTransaction Management Detail Component', () => {
    let comp: InternalTransactionDetailComponent;
    let fixture: ComponentFixture<InternalTransactionDetailComponent>;
    const route = ({ data: of({ internalTransaction: new InternalTransaction(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternalTransactionDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
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
        expect(comp.internalTransaction).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
