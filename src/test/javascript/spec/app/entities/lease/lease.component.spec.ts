import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { LeaseComponent } from 'app/entities/lease/lease.component';
import { LeaseService } from 'app/entities/lease/lease.service';
import { Lease } from 'app/shared/model/lease.model';

describe('Component Tests', () => {
  describe('Lease Management Component', () => {
    let comp: LeaseComponent;
    let fixture: ComponentFixture<LeaseComponent>;
    let service: LeaseService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LeaseComponent],
        providers: []
      })
        .overrideTemplate(LeaseComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LeaseComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LeaseService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Lease(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.leases && comp.leases[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
