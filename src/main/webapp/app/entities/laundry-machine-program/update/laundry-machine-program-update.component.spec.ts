jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';
import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';
import { ILaundryProgram } from 'app/entities/laundry-program/laundry-program.model';
import { LaundryProgramService } from 'app/entities/laundry-program/service/laundry-program.service';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/service/laundry-machine.service';

import { LaundryMachineProgramUpdateComponent } from './laundry-machine-program-update.component';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Update Component', () => {
    let comp: LaundryMachineProgramUpdateComponent;
    let fixture: ComponentFixture<LaundryMachineProgramUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let laundryMachineProgramService: LaundryMachineProgramService;
    let laundryProgramService: LaundryProgramService;
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
      laundryProgramService = TestBed.inject(LaundryProgramService);
      laundryMachineService = TestBed.inject(LaundryMachineService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call LaundryProgram query and add missing value', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 456 };
        const program: ILaundryProgram = { id: 3271 };
        laundryMachineProgram.program = program;

        const laundryProgramCollection: ILaundryProgram[] = [{ id: 92796 }];
        jest.spyOn(laundryProgramService, 'query').mockReturnValue(of(new HttpResponse({ body: laundryProgramCollection })));
        const additionalLaundryPrograms = [program];
        const expectedCollection: ILaundryProgram[] = [...additionalLaundryPrograms, ...laundryProgramCollection];
        jest.spyOn(laundryProgramService, 'addLaundryProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        expect(laundryProgramService.query).toHaveBeenCalled();
        expect(laundryProgramService.addLaundryProgramToCollectionIfMissing).toHaveBeenCalledWith(
          laundryProgramCollection,
          ...additionalLaundryPrograms
        );
        expect(comp.laundryProgramsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call LaundryMachine query and add missing value', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 456 };
        const machine: ILaundryMachine = { id: 50344 };
        laundryMachineProgram.machine = machine;

        const laundryMachineCollection: ILaundryMachine[] = [{ id: 78591 }];
        jest.spyOn(laundryMachineService, 'query').mockReturnValue(of(new HttpResponse({ body: laundryMachineCollection })));
        const additionalLaundryMachines = [machine];
        const expectedCollection: ILaundryMachine[] = [...additionalLaundryMachines, ...laundryMachineCollection];
        jest.spyOn(laundryMachineService, 'addLaundryMachineToCollectionIfMissing').mockReturnValue(expectedCollection);

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
        const program: ILaundryProgram = { id: 15890 };
        laundryMachineProgram.program = program;
        const machine: ILaundryMachine = { id: 52770 };
        laundryMachineProgram.machine = machine;

        activatedRoute.data = of({ laundryMachineProgram });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(laundryMachineProgram));
        expect(comp.laundryProgramsSharedCollection).toContain(program);
        expect(comp.laundryMachinesSharedCollection).toContain(machine);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<LaundryMachineProgram>>();
        const laundryMachineProgram = { id: 123 };
        jest.spyOn(laundryMachineProgramService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
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
        const saveSubject = new Subject<HttpResponse<LaundryMachineProgram>>();
        const laundryMachineProgram = new LaundryMachineProgram();
        jest.spyOn(laundryMachineProgramService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
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
        const saveSubject = new Subject<HttpResponse<LaundryMachineProgram>>();
        const laundryMachineProgram = { id: 123 };
        jest.spyOn(laundryMachineProgramService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
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
      describe('trackLaundryProgramById', () => {
        it('Should return tracked LaundryProgram primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLaundryProgramById(0, entity);
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
    });
  });
});
