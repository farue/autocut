jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ActivityService } from '../service/activity.service';
import { Activity, IActivity } from '../activity.model';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';
import { TeamMembershipService } from 'app/entities/team-membership/service/team-membership.service';

import { ActivityUpdateComponent } from './activity-update.component';

describe('Component Tests', () => {
  describe('Activity Management Update Component', () => {
    let comp: ActivityUpdateComponent;
    let fixture: ComponentFixture<ActivityUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let activityService: ActivityService;
    let tenantService: TenantService;
    let teamMembershipService: TeamMembershipService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ActivityUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ActivityUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ActivityUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      activityService = TestBed.inject(ActivityService);
      tenantService = TestBed.inject(TenantService);
      teamMembershipService = TestBed.inject(TeamMembershipService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Tenant query and add missing value', () => {
        const activity: IActivity = { id: 456 };
        const tenant: ITenant = { id: 92752 };
        activity.tenant = tenant;

        const tenantCollection: ITenant[] = [{ id: 64521 }];
        jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
        const additionalTenants = [tenant];
        const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
        jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(tenantService.query).toHaveBeenCalled();
        expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, ...additionalTenants);
        expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TeamMembership query and add missing value', () => {
        const activity: IActivity = { id: 456 };
        const teamMembership: ITeamMembership = { id: 81646 };
        activity.teamMembership = teamMembership;

        const teamMembershipCollection: ITeamMembership[] = [{ id: 23072 }];
        jest.spyOn(teamMembershipService, 'query').mockReturnValue(of(new HttpResponse({ body: teamMembershipCollection })));
        const additionalTeamMemberships = [teamMembership];
        const expectedCollection: ITeamMembership[] = [...additionalTeamMemberships, ...teamMembershipCollection];
        jest.spyOn(teamMembershipService, 'addTeamMembershipToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(teamMembershipService.query).toHaveBeenCalled();
        expect(teamMembershipService.addTeamMembershipToCollectionIfMissing).toHaveBeenCalledWith(
          teamMembershipCollection,
          ...additionalTeamMemberships
        );
        expect(comp.teamMembershipsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const activity: IActivity = { id: 456 };
        const tenant: ITenant = { id: 54459 };
        activity.tenant = tenant;
        const teamMembership: ITeamMembership = { id: 4415 };
        activity.teamMembership = teamMembership;

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(activity));
        expect(comp.tenantsSharedCollection).toContain(tenant);
        expect(comp.teamMembershipsSharedCollection).toContain(teamMembership);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Activity>>();
        const activity = { id: 123 };
        jest.spyOn(activityService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: activity }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(activityService.update).toHaveBeenCalledWith(activity);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Activity>>();
        const activity = new Activity();
        jest.spyOn(activityService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: activity }));
        saveSubject.complete();

        // THEN
        expect(activityService.create).toHaveBeenCalledWith(activity);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Activity>>();
        const activity = { id: 123 };
        jest.spyOn(activityService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(activityService.update).toHaveBeenCalledWith(activity);
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

      describe('trackTeamMembershipById', () => {
        it('Should return tracked TeamMembership primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTeamMembershipById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
