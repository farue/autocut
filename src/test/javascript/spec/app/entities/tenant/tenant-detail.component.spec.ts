import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TenantDetailComponent } from 'app/entities/tenant/tenant-detail.component';
import { Tenant } from 'app/shared/model/tenant.model';

describe('Component Tests', () => {
  describe('Tenant Management Detail Component', () => {
    let comp: TenantDetailComponent;
    let fixture: ComponentFixture<TenantDetailComponent>;
    const route = ({ data: of({ tenant: new Tenant(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(TenantDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TenantDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.tenant).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
