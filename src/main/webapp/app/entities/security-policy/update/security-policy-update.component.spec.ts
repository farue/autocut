jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SecurityPolicyService } from '../service/security-policy.service';
import { ISecurityPolicy, SecurityPolicy } from '../security-policy.model';
import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';
import { TeamMembershipService } from 'app/entities/team-membership/service/team-membership.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';

import { SecurityPolicyUpdateComponent } from './security-policy-update.component';

describe('Component Tests', () => {
  describe('SecurityPolicy Management Update Component', () => {
    let comp: SecurityPolicyUpdateComponent;
    let fixture: ComponentFixture<SecurityPolicyUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let securityPolicyService: SecurityPolicyService;
    let teamMembershipService: TeamMembershipService;
    let tenantService: TenantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SecurityPolicyUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SecurityPolicyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SecurityPolicyUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      securityPolicyService = TestBed.inject(SecurityPolicyService);
      teamMembershipService = TestBed.inject(TeamMembershipService);
      tenantService = TestBed.inject(TenantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call TeamMembership query and add missing value', () => {
        const securityPolicy: ISecurityPolicy = { id: 456 };
        const teamMember: ITeamMembership = { id: 76455 };
        securityPolicy.teamMember = teamMember;

        const teamMembershipCollection: ITeamMembership[] = [{ id: 66936 }];
        spyOn(teamMembershipService, 'query').and.returnValue(of(new HttpResponse({ body: teamMembershipCollection })));
        const additionalTeamMemberships = [teamMember];
        const expectedCollection: ITeamMembership[] = [...additionalTeamMemberships, ...teamMembershipCollection];
        spyOn(teamMembershipService, 'addTeamMembershipToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ securityPolicy });
        comp.ngOnInit();

        expect(teamMembershipService.query).toHaveBeenCalled();
        expect(teamMembershipService.addTeamMembershipToCollectionIfMissing).toHaveBeenCalledWith(
          teamMembershipCollection,
          ...additionalTeamMemberships
        );
        expect(comp.teamMembershipsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Tenant query and add missing value', () => {
        const securityPolicy: ISecurityPolicy = { id: 456 };
        const tenant: ITenant = { id: 91592 };
        securityPolicy.tenant = tenant;

        const tenantCollection: ITenant[] = [{ id: 4105 }];
        spyOn(tenantService, 'query').and.returnValue(of(new HttpResponse({ body: tenantCollection })));
        const additionalTenants = [tenant];
        const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
        spyOn(tenantService, 'addTenantToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ securityPolicy });
        comp.ngOnInit();

        expect(tenantService.query).toHaveBeenCalled();
        expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, ...additionalTenants);
        expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const securityPolicy: ISecurityPolicy = { id: 456 };
        const teamMember: ITeamMembership = { id: 90870 };
        securityPolicy.teamMember = teamMember;
        const tenant: ITenant = { id: 15631 };
        securityPolicy.tenant = tenant;

        activatedRoute.data = of({ securityPolicy });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(securityPolicy));
        expect(comp.teamMembershipsSharedCollection).toContain(teamMember);
        expect(comp.tenantsSharedCollection).toContain(tenant);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const securityPolicy = { id: 123 };
        spyOn(securityPolicyService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ securityPolicy });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: securityPolicy }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(securityPolicyService.update).toHaveBeenCalledWith(securityPolicy);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const securityPolicy = new SecurityPolicy();
        spyOn(securityPolicyService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ securityPolicy });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: securityPolicy }));
        saveSubject.complete();

        // THEN
        expect(securityPolicyService.create).toHaveBeenCalledWith(securityPolicy);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const securityPolicy = { id: 123 };
        spyOn(securityPolicyService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ securityPolicy });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(securityPolicyService.update).toHaveBeenCalledWith(securityPolicy);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackTeamMembershipById', () => {
        it('Should return tracked TeamMembership primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTeamMembershipById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

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
