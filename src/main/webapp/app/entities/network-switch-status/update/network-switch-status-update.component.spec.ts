jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { NetworkSwitchStatusService } from '../service/network-switch-status.service';
import { INetworkSwitchStatus, NetworkSwitchStatus } from '../network-switch-status.model';
import { INetworkSwitch } from 'app/entities/network-switch/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/service/network-switch.service';

import { NetworkSwitchStatusUpdateComponent } from './network-switch-status-update.component';

describe('Component Tests', () => {
  describe('NetworkSwitchStatus Management Update Component', () => {
    let comp: NetworkSwitchStatusUpdateComponent;
    let fixture: ComponentFixture<NetworkSwitchStatusUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let networkSwitchStatusService: NetworkSwitchStatusService;
    let networkSwitchService: NetworkSwitchService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [NetworkSwitchStatusUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(NetworkSwitchStatusUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NetworkSwitchStatusUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      networkSwitchStatusService = TestBed.inject(NetworkSwitchStatusService);
      networkSwitchService = TestBed.inject(NetworkSwitchService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call NetworkSwitch query and add missing value', () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 456 };
        const networkSwitch: INetworkSwitch = { id: 9748 };
        networkSwitchStatus.networkSwitch = networkSwitch;

        const networkSwitchCollection: INetworkSwitch[] = [{ id: 15357 }];
        jest.spyOn(networkSwitchService, 'query').mockReturnValue(of(new HttpResponse({ body: networkSwitchCollection })));
        const additionalNetworkSwitches = [networkSwitch];
        const expectedCollection: INetworkSwitch[] = [...additionalNetworkSwitches, ...networkSwitchCollection];
        jest.spyOn(networkSwitchService, 'addNetworkSwitchToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ networkSwitchStatus });
        comp.ngOnInit();

        expect(networkSwitchService.query).toHaveBeenCalled();
        expect(networkSwitchService.addNetworkSwitchToCollectionIfMissing).toHaveBeenCalledWith(
          networkSwitchCollection,
          ...additionalNetworkSwitches
        );
        expect(comp.networkSwitchesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 456 };
        const networkSwitch: INetworkSwitch = { id: 44810 };
        networkSwitchStatus.networkSwitch = networkSwitch;

        activatedRoute.data = of({ networkSwitchStatus });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(networkSwitchStatus));
        expect(comp.networkSwitchesSharedCollection).toContain(networkSwitch);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<NetworkSwitchStatus>>();
        const networkSwitchStatus = { id: 123 };
        jest.spyOn(networkSwitchStatusService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ networkSwitchStatus });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: networkSwitchStatus }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(networkSwitchStatusService.update).toHaveBeenCalledWith(networkSwitchStatus);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<NetworkSwitchStatus>>();
        const networkSwitchStatus = new NetworkSwitchStatus();
        jest.spyOn(networkSwitchStatusService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ networkSwitchStatus });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: networkSwitchStatus }));
        saveSubject.complete();

        // THEN
        expect(networkSwitchStatusService.create).toHaveBeenCalledWith(networkSwitchStatus);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<NetworkSwitchStatus>>();
        const networkSwitchStatus = { id: 123 };
        jest.spyOn(networkSwitchStatusService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ networkSwitchStatus });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(networkSwitchStatusService.update).toHaveBeenCalledWith(networkSwitchStatus);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackNetworkSwitchById', () => {
        it('Should return tracked NetworkSwitch primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackNetworkSwitchById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
