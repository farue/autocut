import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TransactionBookService } from '../service/transaction-book.service';

import { TransactionBookComponent } from './transaction-book.component';

describe('Component Tests', () => {
  describe('TransactionBook Management Component', () => {
    let comp: TransactionBookComponent;
    let fixture: ComponentFixture<TransactionBookComponent>;
    let service: TransactionBookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TransactionBookComponent],
      })
        .overrideTemplate(TransactionBookComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TransactionBookComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(TransactionBookService);

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
      expect(comp.transactionBooks?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
