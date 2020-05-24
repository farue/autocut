import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { WashHistoryComponent } from 'app/entities/wash-history/wash-history.component';
import { WashHistoryService } from 'app/entities/wash-history/wash-history.service';
import { WashHistory } from 'app/shared/model/wash-history.model';

describe('Component Tests', () => {
  describe('WashHistory Management Component', () => {
    let comp: WashHistoryComponent;
    let fixture: ComponentFixture<WashHistoryComponent>;
    let service: WashHistoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [WashHistoryComponent],
      })
        .overrideTemplate(WashHistoryComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(WashHistoryComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(WashHistoryService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new WashHistory(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.washHistories && comp.washHistories[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
