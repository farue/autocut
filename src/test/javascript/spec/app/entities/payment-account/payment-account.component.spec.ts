import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { PaymentAccountComponent } from 'app/entities/payment-account/payment-account.component';
import { PaymentAccountService } from 'app/entities/payment-account/payment-account.service';
import { PaymentAccount } from 'app/shared/model/payment-account.model';

describe('Component Tests', () => {
  describe('PaymentAccount Management Component', () => {
    let comp: PaymentAccountComponent;
    let fixture: ComponentFixture<PaymentAccountComponent>;
    let service: PaymentAccountService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentAccountComponent],
        providers: []
      })
        .overrideTemplate(PaymentAccountComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaymentAccountComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PaymentAccountService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new PaymentAccount(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.paymentAccounts && comp.paymentAccounts[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
