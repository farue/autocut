import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { from, of, Subject } from 'rxjs';

import { CommunicationService } from '../service/communication.service';
import { Communication, ICommunication } from '../communication.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { CommunicationUpdateComponent } from './communication-update.component';

describe('Communication Management Update Component', () => {
  let comp: CommunicationUpdateComponent;
  let fixture: ComponentFixture<CommunicationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let communicationService: CommunicationService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommunicationUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CommunicationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommunicationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    communicationService = TestBed.inject(CommunicationService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const communication: ICommunication = { id: 456 };
      const tenant: IUser = { id: 57737 };
      communication.tenant = tenant;

      const userCollection: IUser[] = [{ id: 57251 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [tenant];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ communication });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const communication: ICommunication = { id: 456 };
      const tenant: IUser = { id: 70848 };
      communication.tenant = tenant;

      activatedRoute.data = of({ communication });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(communication));
      expect(comp.usersSharedCollection).toContain(tenant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Communication>>();
      const communication = { id: 123 };
      jest.spyOn(communicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ communication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: communication }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(communicationService.update).toHaveBeenCalledWith(communication);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Communication>>();
      const communication = new Communication();
      jest.spyOn(communicationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ communication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: communication }));
      saveSubject.complete();

      // THEN
      expect(communicationService.create).toHaveBeenCalledWith(communication);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Communication>>();
      const communication = { id: 123 };
      jest.spyOn(communicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ communication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(communicationService.update).toHaveBeenCalledWith(communication);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
