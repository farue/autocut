import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { WashHistoryService } from '../service/wash-history.service';

import { WashHistoryComponent } from './wash-history.component';

describe('Component Tests', () => {
  describe('WashHistory Management Component', () => {
    let comp: WashHistoryComponent;
    let fixture: ComponentFixture<WashHistoryComponent>;
    let service: WashHistoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [WashHistoryComponent],
      })
        .overrideTemplate(WashHistoryComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(WashHistoryComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(WashHistoryService);

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
      expect(comp.washHistories?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
