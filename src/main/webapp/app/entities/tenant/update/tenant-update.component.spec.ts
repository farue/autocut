jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TenantService } from '../service/tenant.service';
import { ITenant, Tenant } from '../tenant.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ILease } from 'app/entities/lease/lease.model';
import { LeaseService } from 'app/entities/lease/service/lease.service';

import { TenantUpdateComponent } from './tenant-update.component';

describe('Component Tests', () => {
  describe('Tenant Management Update Component', () => {
    let comp: TenantUpdateComponent;
    let fixture: ComponentFixture<TenantUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let tenantService: TenantService;
    let userService: UserService;
    let leaseService: LeaseService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TenantUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TenantUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      tenantService = TestBed.inject(TenantService);
      userService = TestBed.inject(UserService);
      leaseService = TestBed.inject(LeaseService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const tenant: ITenant = { id: 456 };
        const user: IUser = { id: 50311 };
        tenant.user = user;

        const userCollection: IUser[] = [{ id: 49568 }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ tenant });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Lease query and add missing value', () => {
        const tenant: ITenant = { id: 456 };
        const lease: ILease = { id: 11732 };
        tenant.lease = lease;

        const leaseCollection: ILease[] = [{ id: 64729 }];
        spyOn(leaseService, 'query').and.returnValue(of(new HttpResponse({ body: leaseCollection })));
        const additionalLeases = [lease];
        const expectedCollection: ILease[] = [...additionalLeases, ...leaseCollection];
        spyOn(leaseService, 'addLeaseToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ tenant });
        comp.ngOnInit();

        expect(leaseService.query).toHaveBeenCalled();
        expect(leaseService.addLeaseToCollectionIfMissing).toHaveBeenCalledWith(leaseCollection, ...additionalLeases);
        expect(comp.leasesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const tenant: ITenant = { id: 456 };
        const user: IUser = { id: 40502 };
        tenant.user = user;
        const lease: ILease = { id: 33505 };
        tenant.lease = lease;

        activatedRoute.data = of({ tenant });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(tenant));
        expect(comp.usersSharedCollection).toContain(user);
        expect(comp.leasesSharedCollection).toContain(lease);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tenant = { id: 123 };
        spyOn(tenantService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tenant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tenant }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(tenantService.update).toHaveBeenCalledWith(tenant);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tenant = new Tenant();
        spyOn(tenantService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tenant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tenant }));
        saveSubject.complete();

        // THEN
        expect(tenantService.create).toHaveBeenCalledWith(tenant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const tenant = { id: 123 };
        spyOn(tenantService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ tenant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(tenantService.update).toHaveBeenCalledWith(tenant);
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

      describe('trackLeaseById', () => {
        it('Should return tracked Lease primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLeaseById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
