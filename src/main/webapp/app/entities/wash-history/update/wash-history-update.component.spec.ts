jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { WashHistoryService } from '../service/wash-history.service';
import { IWashHistory, WashHistory } from '../wash-history.model';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/service/laundry-machine.service';
import { ILaundryMachineProgram } from 'app/entities/laundry-machine-program/laundry-machine-program.model';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/service/laundry-machine-program.service';

import { WashHistoryUpdateComponent } from './wash-history-update.component';

describe('WashHistory Management Update Component', () => {
  let comp: WashHistoryUpdateComponent;
  let fixture: ComponentFixture<WashHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let washHistoryService: WashHistoryService;
  let tenantService: TenantService;
  let laundryMachineService: LaundryMachineService;
  let laundryMachineProgramService: LaundryMachineProgramService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [WashHistoryUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(WashHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WashHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    washHistoryService = TestBed.inject(WashHistoryService);
    tenantService = TestBed.inject(TenantService);
    laundryMachineService = TestBed.inject(LaundryMachineService);
    laundryMachineProgramService = TestBed.inject(LaundryMachineProgramService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tenant query and add missing value', () => {
      const washHistory: IWashHistory = { id: 456 };
      const reservationTenant: ITenant = { id: 94577 };
      washHistory.reservationTenant = reservationTenant;
      const usingTenant: ITenant = { id: 23614 };
      washHistory.usingTenant = usingTenant;

      const tenantCollection: ITenant[] = [{ id: 50091 }];
      jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
      const additionalTenants = [reservationTenant, usingTenant];
      const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
      jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      expect(tenantService.query).toHaveBeenCalled();
      expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, ...additionalTenants);
      expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call LaundryMachine query and add missing value', () => {
      const washHistory: IWashHistory = { id: 456 };
      const machine: ILaundryMachine = { id: 84899 };
      washHistory.machine = machine;

      const laundryMachineCollection: ILaundryMachine[] = [{ id: 86726 }];
      jest.spyOn(laundryMachineService, 'query').mockReturnValue(of(new HttpResponse({ body: laundryMachineCollection })));
      const additionalLaundryMachines = [machine];
      const expectedCollection: ILaundryMachine[] = [...additionalLaundryMachines, ...laundryMachineCollection];
      jest.spyOn(laundryMachineService, 'addLaundryMachineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      expect(laundryMachineService.query).toHaveBeenCalled();
      expect(laundryMachineService.addLaundryMachineToCollectionIfMissing).toHaveBeenCalledWith(
        laundryMachineCollection,
        ...additionalLaundryMachines
      );
      expect(comp.laundryMachinesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call LaundryMachineProgram query and add missing value', () => {
      const washHistory: IWashHistory = { id: 456 };
      const program: ILaundryMachineProgram = { id: 82030 };
      washHistory.program = program;

      const laundryMachineProgramCollection: ILaundryMachineProgram[] = [{ id: 34346 }];
      jest.spyOn(laundryMachineProgramService, 'query').mockReturnValue(of(new HttpResponse({ body: laundryMachineProgramCollection })));
      const additionalLaundryMachinePrograms = [program];
      const expectedCollection: ILaundryMachineProgram[] = [...additionalLaundryMachinePrograms, ...laundryMachineProgramCollection];
      jest.spyOn(laundryMachineProgramService, 'addLaundryMachineProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      expect(laundryMachineProgramService.query).toHaveBeenCalled();
      expect(laundryMachineProgramService.addLaundryMachineProgramToCollectionIfMissing).toHaveBeenCalledWith(
        laundryMachineProgramCollection,
        ...additionalLaundryMachinePrograms
      );
      expect(comp.laundryMachineProgramsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const washHistory: IWashHistory = { id: 456 };
      const reservationTenant: ITenant = { id: 19910 };
      washHistory.reservationTenant = reservationTenant;
      const usingTenant: ITenant = { id: 65986 };
      washHistory.usingTenant = usingTenant;
      const machine: ILaundryMachine = { id: 29871 };
      washHistory.machine = machine;
      const program: ILaundryMachineProgram = { id: 88993 };
      washHistory.program = program;

      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(washHistory));
      expect(comp.tenantsSharedCollection).toContain(reservationTenant);
      expect(comp.tenantsSharedCollection).toContain(usingTenant);
      expect(comp.laundryMachinesSharedCollection).toContain(machine);
      expect(comp.laundryMachineProgramsSharedCollection).toContain(program);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<WashHistory>>();
      const washHistory = { id: 123 };
      jest.spyOn(washHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: washHistory }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(washHistoryService.update).toHaveBeenCalledWith(washHistory);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<WashHistory>>();
      const washHistory = new WashHistory();
      jest.spyOn(washHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: washHistory }));
      saveSubject.complete();

      // THEN
      expect(washHistoryService.create).toHaveBeenCalledWith(washHistory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<WashHistory>>();
      const washHistory = { id: 123 };
      jest.spyOn(washHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ washHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(washHistoryService.update).toHaveBeenCalledWith(washHistory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTenantById', () => {
      it('Should return tracked Tenant primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTenantById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackLaundryMachineById', () => {
      it('Should return tracked LaundryMachine primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLaundryMachineById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackLaundryMachineProgramById', () => {
      it('Should return tracked LaundryMachineProgram primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLaundryMachineProgramById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
