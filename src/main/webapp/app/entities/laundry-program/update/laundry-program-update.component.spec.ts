jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LaundryProgramService } from '../service/laundry-program.service';
import { ILaundryProgram, LaundryProgram } from '../laundry-program.model';

import { LaundryProgramUpdateComponent } from './laundry-program-update.component';

describe('LaundryProgram Management Update Component', () => {
  let comp: LaundryProgramUpdateComponent;
  let fixture: ComponentFixture<LaundryProgramUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let laundryProgramService: LaundryProgramService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LaundryProgramUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(LaundryProgramUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LaundryProgramUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    laundryProgramService = TestBed.inject(LaundryProgramService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const laundryProgram: ILaundryProgram = { id: 456 };

      activatedRoute.data = of({ laundryProgram });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(laundryProgram));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LaundryProgram>>();
      const laundryProgram = { id: 123 };
      jest.spyOn(laundryProgramService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ laundryProgram });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: laundryProgram }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(laundryProgramService.update).toHaveBeenCalledWith(laundryProgram);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LaundryProgram>>();
      const laundryProgram = new LaundryProgram();
      jest.spyOn(laundryProgramService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ laundryProgram });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: laundryProgram }));
      saveSubject.complete();

      // THEN
      expect(laundryProgramService.create).toHaveBeenCalledWith(laundryProgram);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LaundryProgram>>();
      const laundryProgram = { id: 123 };
      jest.spyOn(laundryProgramService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ laundryProgram });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(laundryProgramService.update).toHaveBeenCalledWith(laundryProgram);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
