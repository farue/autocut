import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { TenantComponent } from 'app/entities/tenant/tenant.component';
import { TenantService } from 'app/entities/tenant/tenant.service';
import { Tenant } from 'app/shared/model/tenant.model';

describe('Component Tests', () => {
  describe('Tenant Management Component', () => {
    let comp: TenantComponent;
    let fixture: ComponentFixture<TenantComponent>;
    let service: TenantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantComponent]
      })
        .overrideTemplate(TenantComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TenantService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Tenant(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.tenants && comp.tenants[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
