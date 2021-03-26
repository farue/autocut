jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';
import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/service/laundry-machine.service';

import { LaundryMachineProgramUpdateComponent } from './laundry-machine-program-update.component';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Update Component', () => {
    let comp: LaundryMachineProgramUpdateComponent;
    let fixture: ComponentFixture<LaundryMachineProgramUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let laundryMachineProgramService: LaundryMachineProgramService;
    let laundryMachineService: LaundryMachineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LaundryMachineProgramUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(LaundryMachineProgramUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryMachineProgramUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      laundryMachineProgramService = TestBed.inject(LaundryMachineProgramService);
      laundryMachineService = TestBed.inject(LaundryMachineService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call LaundryMachine query and add missing value', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 456 };
        const laundryMachine: ILaundryMachine = { id: 53251 };
        laundryMachineProgram.laundryMachine = laundryMachine;

        const laundryMachineCollection: ILaundryMachine[] = [{ id: 96702 }];
        spyOn(laundryMachineService, 'query').and.returnValue(of(new HttpResponse({ body: laundryMachineCollection })));
        const additionalLaundryMachines = [laundryMachine];
        const expectedCollection: ILaundryMachine[] = [...additionalLaundryMachines, ...laundryMachineCollection];
        spyOn(laundryMachineService, 'addLaundryMachineToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        expect(laundryMachineService.query).toHaveBeenCalled();
        expect(laundryMachineService.addLaundryMachineToCollectionIfMissing).toHaveBeenCalledWith(
          laundryMachineCollection,
          ...additionalLaundryMachines
        );
        expect(comp.laundryMachinesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 456 };
        const laundryMachine: ILaundryMachine = { id: 63927 };
        laundryMachineProgram.laundryMachine = laundryMachine;

        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(laundryMachineProgram));
        expect(comp.laundryMachinesSharedCollection).toContain(laundryMachine);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const laundryMachineProgram = { id: 123 };
        spyOn(laundryMachineProgramService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: laundryMachineProgram }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(laundryMachineProgramService.update).toHaveBeenCalledWith(laundryMachineProgram);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const laundryMachineProgram = new LaundryMachineProgram();
        spyOn(laundryMachineProgramService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: laundryMachineProgram }));
        saveSubject.complete();

        // THEN
        expect(laundryMachineProgramService.create).toHaveBeenCalledWith(laundryMachineProgram);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const laundryMachineProgram = { id: 123 };
        spyOn(laundryMachineProgramService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(laundryMachineProgramService.update).toHaveBeenCalledWith(laundryMachineProgram);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackLaundryMachineById', () => {
        it('Should return tracked LaundryMachine primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLaundryMachineById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
