import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { CoinUpdateComponent } from 'app/entities/coin/coin-update.component';
import { CoinService } from 'app/entities/coin/coin.service';
import { Coin } from 'app/shared/model/coin.model';

describe('Component Tests', () => {
  describe('Coin Management Update Component', () => {
    let comp: CoinUpdateComponent;
    let fixture: ComponentFixture<CoinUpdateComponent>;
    let service: CoinService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [CoinUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(CoinUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CoinUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CoinService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Coin(123);
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
        const entity = new Coin();
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
