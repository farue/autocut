import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { InternetAccessComponent } from 'app/entities/internet-access/internet-access.component';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';
import { InternetAccess } from 'app/shared/model/internet-access.model';

describe('Component Tests', () => {
  describe('InternetAccess Management Component', () => {
    let comp: InternetAccessComponent;
    let fixture: ComponentFixture<InternetAccessComponent>;
    let service: InternetAccessService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternetAccessComponent]
      })
        .overrideTemplate(InternetAccessComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternetAccessComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InternetAccessService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new InternetAccess(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.internetAccesses && comp.internetAccesses[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
