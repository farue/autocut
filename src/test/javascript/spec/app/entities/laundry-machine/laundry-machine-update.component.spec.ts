import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { LaundryMachineUpdateComponent } from 'app/entities/laundry-machine/laundry-machine-update.component';
import { LaundryMachineService } from 'app/entities/laundry-machine/laundry-machine.service';
import { LaundryMachine } from 'app/shared/model/laundry-machine.model';

describe('Component Tests', () => {
  describe('LaundryMachine Management Update Component', () => {
    let comp: LaundryMachineUpdateComponent;
    let fixture: ComponentFixture<LaundryMachineUpdateComponent>;
    let service: LaundryMachineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(LaundryMachineUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryMachineUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LaundryMachineService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new LaundryMachine(123);
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
        const entity = new LaundryMachine();
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
