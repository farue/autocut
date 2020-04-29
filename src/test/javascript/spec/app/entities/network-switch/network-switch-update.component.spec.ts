import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchUpdateComponent } from 'app/entities/network-switch/network-switch-update.component';
import { NetworkSwitchService } from 'app/entities/network-switch/network-switch.service';
import { NetworkSwitch } from 'app/shared/model/network-switch.model';

describe('Component Tests', () => {
  describe('NetworkSwitch Management Update Component', () => {
    let comp: NetworkSwitchUpdateComponent;
    let fixture: ComponentFixture<NetworkSwitchUpdateComponent>;
    let service: NetworkSwitchService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(NetworkSwitchUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NetworkSwitchUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NetworkSwitchService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new NetworkSwitch(123);
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
        const entity = new NetworkSwitch();
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
