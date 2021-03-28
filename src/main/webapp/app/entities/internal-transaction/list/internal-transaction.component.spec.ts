import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { InternalTransactionService } from '../service/internal-transaction.service';

import { InternalTransactionComponent } from './internal-transaction.component';

describe('Component Tests', () => {
  describe('InternalTransaction Management Component', () => {
    let comp: InternalTransactionComponent;
    let fixture: ComponentFixture<InternalTransactionComponent>;
    let service: InternalTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InternalTransactionComponent],
      })
        .overrideTemplate(InternalTransactionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternalTransactionComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(InternalTransactionService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.internalTransactions?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
