jest.mock('@angular/router');

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {of, Subject} from 'rxjs';

import {BroadcastMessageService} from '../service/broadcast-message.service';
import {BroadcastMessage, IBroadcastMessage} from '../broadcast-message.model';

import {BroadcastMessageUpdateComponent} from './broadcast-message-update.component';

describe('Component Tests', () => {
  describe('BroadcastMessage Management Update Component', () => {
    let comp: BroadcastMessageUpdateComponent;
    let fixture: ComponentFixture<BroadcastMessageUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let broadcastMessageService: BroadcastMessageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BroadcastMessageUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BroadcastMessageUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BroadcastMessageUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      broadcastMessageService = TestBed.inject(BroadcastMessageService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const broadcastMessage: IBroadcastMessage = { id: 456 };

        activatedRoute.data = of({ broadcastMessage });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(broadcastMessage));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BroadcastMessage>>();
        const broadcastMessage = { id: 123 };
        jest.spyOn(broadcastMessageService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ broadcastMessage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: broadcastMessage }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(broadcastMessageService.update).toHaveBeenCalledWith(broadcastMessage);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BroadcastMessage>>();
        const broadcastMessage = new BroadcastMessage();
        jest.spyOn(broadcastMessageService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ broadcastMessage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: broadcastMessage }));
        saveSubject.complete();

        // THEN
        expect(broadcastMessageService.create).toHaveBeenCalledWith(broadcastMessage);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BroadcastMessage>>();
        const broadcastMessage = { id: 123 };
        jest.spyOn(broadcastMessageService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ broadcastMessage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(broadcastMessageService.update).toHaveBeenCalledWith(broadcastMessage);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
