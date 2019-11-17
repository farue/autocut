import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TenantCommunicationDetailComponent } from 'app/entities/tenant-communication/tenant-communication-detail.component';
import { TenantCommunication } from 'app/shared/model/tenant-communication.model';

describe('Component Tests', () => {
  describe('TenantCommunication Management Detail Component', () => {
    let comp: TenantCommunicationDetailComponent;
    let fixture: ComponentFixture<TenantCommunicationDetailComponent>;
    const route = ({ data: of({ tenantCommunication: new TenantCommunication(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantCommunicationDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(TenantCommunicationDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TenantCommunicationDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.tenantCommunication).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
