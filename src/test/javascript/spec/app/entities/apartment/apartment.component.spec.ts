import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { ApartmentComponent } from 'app/entities/apartment/apartment.component';
import { ApartmentService } from 'app/entities/apartment/apartment.service';
import { Apartment } from 'app/shared/model/apartment.model';

describe('Component Tests', () => {
  describe('Apartment Management Component', () => {
    let comp: ApartmentComponent;
    let fixture: ComponentFixture<ApartmentComponent>;
    let service: ApartmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [ApartmentComponent],
        providers: []
      })
        .overrideTemplate(ApartmentComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApartmentComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ApartmentService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Apartment(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.apartments && comp.apartments[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
