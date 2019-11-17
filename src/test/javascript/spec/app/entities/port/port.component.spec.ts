import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { PortComponent } from 'app/entities/port/port.component';
import { PortService } from 'app/entities/port/port.service';
import { Port } from 'app/shared/model/port.model';

describe('Component Tests', () => {
  describe('Port Management Component', () => {
    let comp: PortComponent;
    let fixture: ComponentFixture<PortComponent>;
    let service: PortService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [PortComponent],
        providers: []
      })
        .overrideTemplate(PortComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PortComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PortService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Port(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.ports[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
