jest.mock('@angular/router');

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {of, Subject} from 'rxjs';

import {TenantCommunicationService} from '../service/tenant-communication.service';
import {ITenantCommunication, TenantCommunication} from '../tenant-communication.model';
import {ITenant} from 'app/entities/tenant/tenant.model';
import {TenantService} from 'app/entities/tenant/service/tenant.service';

import {TenantCommunicationUpdateComponent} from './tenant-communication-update.component';

describe('Component Tests', () => {
  describe('TenantCommunication Management Update Component', () => {
    let comp: TenantCommunicationUpdateComponent;
    let fixture: ComponentFixture<TenantCommunicationUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let tenantCommunicationService: TenantCommunicationService;
    let tenantService: TenantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TenantCommunicationUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TenantCommunicationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantCommunicationUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      tenantCommunicationService = TestBed.inject(TenantCommunicationService);
      tenantService = TestBed.inject(TenantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Tenant query and add missing value', () => {
        const tenantCommunication: ITenantCommunication = { id: 456 };
        const tenant: ITenant = { id: 31081 };
        tenantCommunication.tenant = tenant;

        const tenantCollection: ITenant[] = [{ id: 90536 }];
        jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
        const additionalTenants = [tenant];
        const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
        jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ tenantCommunication });
        comp.ngOnInit();

        expect(tenantService.query).toHaveBeenCalled();
        expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, ...additionalTenants);
        expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const tenantCommunication: ITenantCommunication = { id: 456 };
        const tenant: ITenant = { id: 33727 };
        tenantCommunication.tenant = tenant;

        activatedRoute.data = of({ tenantCommunication });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(tenantCommunication));
        expect(comp.tenantsSharedCollection).toContain(tenant);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TenantCommunication>>();
        const tenantCommunication = { id: 123 };
        jest.spyOn(tenantCommunicationService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ tenantCommunication });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tenantCommunication }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(tenantCommunicationService.update).toHaveBeenCalledWith(tenantCommunication);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TenantCommunication>>();
        const tenantCommunication = new TenantCommunication();
        jest.spyOn(tenantCommunicationService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ tenantCommunication });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: tenantCommunication }));
        saveSubject.complete();

        // THEN
        expect(tenantCommunicationService.create).toHaveBeenCalledWith(tenantCommunication);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TenantCommunication>>();
        const tenantCommunication = { id: 123 };
        jest.spyOn(tenantCommunicationService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ tenantCommunication });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(tenantCommunicationService.update).toHaveBeenCalledWith(tenantCommunication);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackTenantById', () => {
        it('Should return tracked Tenant primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTenantById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
