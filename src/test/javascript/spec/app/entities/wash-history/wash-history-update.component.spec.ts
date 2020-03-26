import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { WashHistoryUpdateComponent } from 'app/entities/wash-history/wash-history-update.component';
import { WashHistoryService } from 'app/entities/wash-history/wash-history.service';
import { WashHistory } from 'app/shared/model/wash-history.model';

describe('Component Tests', () => {
  describe('WashHistory Management Update Component', () => {
    let comp: WashHistoryUpdateComponent;
    let fixture: ComponentFixture<WashHistoryUpdateComponent>;
    let service: WashHistoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [WashHistoryUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(WashHistoryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(WashHistoryUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(WashHistoryService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new WashHistory(123);
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
        const entity = new WashHistory();
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
