jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { InternetAccessService } from '../service/internet-access.service';
import { IInternetAccess, InternetAccess } from '../internet-access.model';
import { INetworkSwitch } from 'app/entities/network-switch/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/service/network-switch.service';

import { InternetAccessUpdateComponent } from './internet-access-update.component';

describe('Component Tests', () => {
  describe('InternetAccess Management Update Component', () => {
    let comp: InternetAccessUpdateComponent;
    let fixture: ComponentFixture<InternetAccessUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let internetAccessService: InternetAccessService;
    let networkSwitchService: NetworkSwitchService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InternetAccessUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(InternetAccessUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternetAccessUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      internetAccessService = TestBed.inject(InternetAccessService);
      networkSwitchService = TestBed.inject(NetworkSwitchService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call NetworkSwitch query and add missing value', () => {
        const internetAccess: IInternetAccess = { id: 456 };
        const networkSwitch: INetworkSwitch = { id: 62951 };
        internetAccess.networkSwitch = networkSwitch;

        const networkSwitchCollection: INetworkSwitch[] = [{ id: 62261 }];
        spyOn(networkSwitchService, 'query').and.returnValue(of(new HttpResponse({ body: networkSwitchCollection })));
        const additionalNetworkSwitches = [networkSwitch];
        const expectedCollection: INetworkSwitch[] = [...additionalNetworkSwitches, ...networkSwitchCollection];
        spyOn(networkSwitchService, 'addNetworkSwitchToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ internetAccess });
        comp.ngOnInit();

        expect(networkSwitchService.query).toHaveBeenCalled();
        expect(networkSwitchService.addNetworkSwitchToCollectionIfMissing).toHaveBeenCalledWith(
          networkSwitchCollection,
          ...additionalNetworkSwitches
        );
        expect(comp.networkSwitchesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const internetAccess: IInternetAccess = { id: 456 };
        const networkSwitch: INetworkSwitch = { id: 4040 };
        internetAccess.networkSwitch = networkSwitch;

        activatedRoute.data = of({ internetAccess });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(internetAccess));
        expect(comp.networkSwitchesSharedCollection).toContain(networkSwitch);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const internetAccess = { id: 123 };
        spyOn(internetAccessService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ internetAccess });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: internetAccess }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(internetAccessService.update).toHaveBeenCalledWith(internetAccess);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const internetAccess = new InternetAccess();
        spyOn(internetAccessService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ internetAccess });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: internetAccess }));
        saveSubject.complete();

        // THEN
        expect(internetAccessService.create).toHaveBeenCalledWith(internetAccess);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const internetAccess = { id: 123 };
        spyOn(internetAccessService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ internetAccess });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(internetAccessService.update).toHaveBeenCalledWith(internetAccess);
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
