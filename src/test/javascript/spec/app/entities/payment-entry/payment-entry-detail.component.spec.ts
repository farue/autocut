import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { PaymentEntryDetailComponent } from 'app/entities/payment-entry/payment-entry-detail.component';
import { PaymentEntry } from 'app/shared/model/payment-entry.model';

describe('Component Tests', () => {
  describe('PaymentEntry Management Detail Component', () => {
    let comp: PaymentEntryDetailComponent;
    let fixture: ComponentFixture<PaymentEntryDetailComponent>;
    const route = ({ data: of({ paymentEntry: new PaymentEntry(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentEntryDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(PaymentEntryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PaymentEntryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load paymentEntry on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.paymentEntry).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
