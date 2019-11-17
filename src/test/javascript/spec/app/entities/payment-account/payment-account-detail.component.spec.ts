import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { PaymentAccountDetailComponent } from 'app/entities/payment-account/payment-account-detail.component';
import { PaymentAccount } from 'app/shared/model/payment-account.model';

describe('Component Tests', () => {
  describe('PaymentAccount Management Detail Component', () => {
    let comp: PaymentAccountDetailComponent;
    let fixture: ComponentFixture<PaymentAccountDetailComponent>;
    const route = ({ data: of({ paymentAccount: new PaymentAccount(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentAccountDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(PaymentAccountDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PaymentAccountDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.paymentAccount).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
