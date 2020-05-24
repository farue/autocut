import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchDetailComponent } from 'app/entities/network-switch/network-switch-detail.component';
import { NetworkSwitch } from 'app/shared/model/network-switch.model';

describe('Component Tests', () => {
  describe('NetworkSwitch Management Detail Component', () => {
    let comp: NetworkSwitchDetailComponent;
    let fixture: ComponentFixture<NetworkSwitchDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ networkSwitch: new NetworkSwitch(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(NetworkSwitchDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(NetworkSwitchDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load networkSwitch on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.networkSwitch).toEqual(jasmine.objectContaining({ id: 123 }));
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
