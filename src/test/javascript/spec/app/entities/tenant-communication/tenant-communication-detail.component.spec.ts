import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { TenantCommunicationDetailComponent } from 'app/entities/tenant-communication/tenant-communication-detail.component';
import { TenantCommunication } from 'app/shared/model/tenant-communication.model';

describe('Component Tests', () => {
  describe('TenantCommunication Management Detail Component', () => {
    let comp: TenantCommunicationDetailComponent;
    let fixture: ComponentFixture<TenantCommunicationDetailComponent>;
    let dataUtils: JhiDataUtils;
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
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load tenantCommunication on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.tenantCommunication).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
