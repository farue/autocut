jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LaundryMachineService } from '../service/laundry-machine.service';
import { ILaundryMachine, LaundryMachine } from '../laundry-machine.model';

import { LaundryMachineUpdateComponent } from './laundry-machine-update.component';

describe('Component Tests', () => {
  describe('LaundryMachine Management Update Component', () => {
    let comp: LaundryMachineUpdateComponent;
    let fixture: ComponentFixture<LaundryMachineUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let laundryMachineService: LaundryMachineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LaundryMachineUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(LaundryMachineUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryMachineUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      laundryMachineService = TestBed.inject(LaundryMachineService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const laundryMachine: ILaundryMachine = { id: 456 };

        activatedRoute.data = of({ laundryMachine });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(laundryMachine));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const laundryMachine = { id: 123 };
        spyOn(laundryMachineService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ laundryMachine });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: laundryMachine }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(laundryMachineService.update).toHaveBeenCalledWith(laundryMachine);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const laundryMachine = new LaundryMachine();
        spyOn(laundryMachineService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ laundryMachine });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: laundryMachine }));
        saveSubject.complete();

        // THEN
        expect(laundryMachineService.create).toHaveBeenCalledWith(laundryMachine);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const laundryMachine = { id: 123 };
        spyOn(laundryMachineService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ laundryMachine });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(laundryMachineService.update).toHaveBeenCalledWith(laundryMachine);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
