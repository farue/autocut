import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ApartmentService } from '../service/apartment.service';

import { ApartmentComponent } from './apartment.component';

describe('Component Tests', () => {
  describe('Apartment Management Component', () => {
    let comp: ApartmentComponent;
    let fixture: ComponentFixture<ApartmentComponent>;
    let service: ApartmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApartmentComponent],
      })
        .overrideTemplate(ApartmentComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApartmentComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ApartmentService);

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
      expect(comp.apartments?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
