jest.mock('@angular/router');

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {of, Subject} from 'rxjs';

import {NetworkSwitchService} from '../service/network-switch.service';
import {INetworkSwitch, NetworkSwitch} from '../network-switch.model';

import {NetworkSwitchUpdateComponent} from './network-switch-update.component';

describe('Component Tests', () => {
  describe('NetworkSwitch Management Update Component', () => {
    let comp: NetworkSwitchUpdateComponent;
    let fixture: ComponentFixture<NetworkSwitchUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let networkSwitchService: NetworkSwitchService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [NetworkSwitchUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(NetworkSwitchUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NetworkSwitchUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      networkSwitchService = TestBed.inject(NetworkSwitchService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const networkSwitch: INetworkSwitch = { id: 456 };

        activatedRoute.data = of({ networkSwitch });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(networkSwitch));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<NetworkSwitch>>();
        const networkSwitch = { id: 123 };
        jest.spyOn(networkSwitchService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ networkSwitch });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: networkSwitch }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(networkSwitchService.update).toHaveBeenCalledWith(networkSwitch);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<NetworkSwitch>>();
        const networkSwitch = new NetworkSwitch();
        jest.spyOn(networkSwitchService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ networkSwitch });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: networkSwitch }));
        saveSubject.complete();

        // THEN
        expect(networkSwitchService.create).toHaveBeenCalledWith(networkSwitch);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<NetworkSwitch>>();
        const networkSwitch = { id: 123 };
        jest.spyOn(networkSwitchService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ networkSwitch });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(networkSwitchService.update).toHaveBeenCalledWith(networkSwitch);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
