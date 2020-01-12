import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { PaymentEntryComponent } from 'app/entities/payment-entry/payment-entry.component';
import { PaymentEntryService } from 'app/entities/payment-entry/payment-entry.service';
import { PaymentEntry } from 'app/shared/model/payment-entry.model';

describe('Component Tests', () => {
  describe('PaymentEntry Management Component', () => {
    let comp: PaymentEntryComponent;
    let fixture: ComponentFixture<PaymentEntryComponent>;
    let service: PaymentEntryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PaymentEntryComponent],
        providers: []
      })
        .overrideTemplate(PaymentEntryComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaymentEntryComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PaymentEntryService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new PaymentEntry(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.paymentEntries && comp.paymentEntries[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
