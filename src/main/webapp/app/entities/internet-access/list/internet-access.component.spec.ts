import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { InternetAccessService } from '../service/internet-access.service';

import { InternetAccessComponent } from './internet-access.component';

describe('Component Tests', () => {
  describe('InternetAccess Management Component', () => {
    let comp: InternetAccessComponent;
    let fixture: ComponentFixture<InternetAccessComponent>;
    let service: InternetAccessService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InternetAccessComponent],
      })
        .overrideTemplate(InternetAccessComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternetAccessComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(InternetAccessService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
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
      expect(comp.internetAccesses?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
