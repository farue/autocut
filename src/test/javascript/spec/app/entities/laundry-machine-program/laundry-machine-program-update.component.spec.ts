import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { LaundryMachineProgramUpdateComponent } from 'app/entities/laundry-machine-program/laundry-machine-program-update.component';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/laundry-machine-program.service';
import { LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Update Component', () => {
    let comp: LaundryMachineProgramUpdateComponent;
    let fixture: ComponentFixture<LaundryMachineProgramUpdateComponent>;
    let service: LaundryMachineProgramService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineProgramUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(LaundryMachineProgramUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryMachineProgramUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LaundryMachineProgramService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new LaundryMachineProgram(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new LaundryMachineProgram();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
