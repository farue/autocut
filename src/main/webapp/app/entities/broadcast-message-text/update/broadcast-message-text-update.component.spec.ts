jest.mock('@angular/router');

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {of, Subject} from 'rxjs';

import {BroadcastMessageTextService} from '../service/broadcast-message-text.service';
import {BroadcastMessageText, IBroadcastMessageText} from '../broadcast-message-text.model';
import {IBroadcastMessage} from 'app/entities/broadcast-message/broadcast-message.model';
import {BroadcastMessageService} from 'app/entities/broadcast-message/service/broadcast-message.service';

import {BroadcastMessageTextUpdateComponent} from './broadcast-message-text-update.component';

describe('Component Tests', () => {
  describe('BroadcastMessageText Management Update Component', () => {
    let comp: BroadcastMessageTextUpdateComponent;
    let fixture: ComponentFixture<BroadcastMessageTextUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let broadcastMessageTextService: BroadcastMessageTextService;
    let broadcastMessageService: BroadcastMessageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BroadcastMessageTextUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BroadcastMessageTextUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BroadcastMessageTextUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      broadcastMessageTextService = TestBed.inject(BroadcastMessageTextService);
      broadcastMessageService = TestBed.inject(BroadcastMessageService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call BroadcastMessage query and add missing value', () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 456 };
        const message: IBroadcastMessage = { id: 57895 };
        broadcastMessageText.message = message;

        const broadcastMessageCollection: IBroadcastMessage[] = [{ id: 78089 }];
        jest.spyOn(broadcastMessageService, 'query').mockReturnValue(of(new HttpResponse({ body: broadcastMessageCollection })));
        const additionalBroadcastMessages = [message];
        const expectedCollection: IBroadcastMessage[] = [...additionalBroadcastMessages, ...broadcastMessageCollection];
        jest.spyOn(broadcastMessageService, 'addBroadcastMessageToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ broadcastMessageText });
        comp.ngOnInit();

        expect(broadcastMessageService.query).toHaveBeenCalled();
        expect(broadcastMessageService.addBroadcastMessageToCollectionIfMissing).toHaveBeenCalledWith(
          broadcastMessageCollection,
          ...additionalBroadcastMessages
        );
        expect(comp.broadcastMessagesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 456 };
        const message: IBroadcastMessage = { id: 68575 };
        broadcastMessageText.message = message;

        activatedRoute.data = of({ broadcastMessageText });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(broadcastMessageText));
        expect(comp.broadcastMessagesSharedCollection).toContain(message);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BroadcastMessageText>>();
        const broadcastMessageText = { id: 123 };
        jest.spyOn(broadcastMessageTextService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ broadcastMessageText });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: broadcastMessageText }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(broadcastMessageTextService.update).toHaveBeenCalledWith(broadcastMessageText);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BroadcastMessageText>>();
        const broadcastMessageText = new BroadcastMessageText();
        jest.spyOn(broadcastMessageTextService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ broadcastMessageText });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: broadcastMessageText }));
        saveSubject.complete();

        // THEN
        expect(broadcastMessageTextService.create).toHaveBeenCalledWith(broadcastMessageText);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BroadcastMessageText>>();
        const broadcastMessageText = { id: 123 };
        jest.spyOn(broadcastMessageTextService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ broadcastMessageText });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(broadcastMessageTextService.update).toHaveBeenCalledWith(broadcastMessageText);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackBroadcastMessageById', () => {
        it('Should return tracked BroadcastMessage primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBroadcastMessageById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
