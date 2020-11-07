import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { InternalTransactionComponent } from 'app/entities/internal-transaction/internal-transaction.component';
import { InternalTransactionService } from 'app/entities/internal-transaction/internal-transaction.service';
import { InternalTransaction } from 'app/shared/model/internal-transaction.model';

describe('Component Tests', () => {
  describe('InternalTransaction Management Component', () => {
    let comp: InternalTransactionComponent;
    let fixture: ComponentFixture<InternalTransactionComponent>;
    let service: InternalTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternalTransactionComponent],
      })
        .overrideTemplate(InternalTransactionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternalTransactionComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InternalTransactionService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new InternalTransaction(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.internalTransactions && comp.internalTransactions[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
