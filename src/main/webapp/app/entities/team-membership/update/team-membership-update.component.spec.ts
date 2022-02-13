import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { from, of, Subject } from 'rxjs';

import { TeamMembershipService } from '../service/team-membership.service';
import { ITeamMembership, TeamMembership } from '../team-membership.model';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';

import { TeamMembershipUpdateComponent } from './team-membership-update.component';

describe('TeamMembership Management Update Component', () => {
  let comp: TeamMembershipUpdateComponent;
  let fixture: ComponentFixture<TeamMembershipUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamMembershipService: TeamMembershipService;
  let tenantService: TenantService;
  let teamService: TeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TeamMembershipUpdateComponent],
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
      .overrideTemplate(TeamMembershipUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamMembershipUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamMembershipService = TestBed.inject(TeamMembershipService);
    tenantService = TestBed.inject(TenantService);
    teamService = TestBed.inject(TeamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tenant query and add missing value', () => {
      const teamMembership: ITeamMembership = { id: 456 };
      const tenant: ITenant = { id: 26731 };
      teamMembership.tenant = tenant;

      const tenantCollection: ITenant[] = [{ id: 70262 }];
      jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
      const additionalTenants = [tenant];
      const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
      jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamMembership });
      comp.ngOnInit();

      expect(tenantService.query).toHaveBeenCalled();
      expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, ...additionalTenants);
      expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Team query and add missing value', () => {
      const teamMembership: ITeamMembership = { id: 456 };
      const team: ITeam = { id: 79573 };
      teamMembership.team = team;

      const teamCollection: ITeam[] = [{ id: 58812 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeams = [team];
      const expectedCollection: ITeam[] = [...additionalTeams, ...teamCollection];
      jest.spyOn(teamService, 'addTeamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamMembership });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamToCollectionIfMissing).toHaveBeenCalledWith(teamCollection, ...additionalTeams);
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamMembership: ITeamMembership = { id: 456 };
      const tenant: ITenant = { id: 94593 };
      teamMembership.tenant = tenant;
      const team: ITeam = { id: 15888 };
      teamMembership.team = team;

      activatedRoute.data = of({ teamMembership });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(teamMembership));
      expect(comp.tenantsSharedCollection).toContain(tenant);
      expect(comp.teamsSharedCollection).toContain(team);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TeamMembership>>();
      const teamMembership = { id: 123 };
      jest.spyOn(teamMembershipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamMembership });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamMembership }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamMembershipService.update).toHaveBeenCalledWith(teamMembership);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TeamMembership>>();
      const teamMembership = new TeamMembership();
      jest.spyOn(teamMembershipService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamMembership });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamMembership }));
      saveSubject.complete();

      // THEN
      expect(teamMembershipService.create).toHaveBeenCalledWith(teamMembership);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TeamMembership>>();
      const teamMembership = { id: 123 };
      jest.spyOn(teamMembershipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamMembership });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamMembershipService.update).toHaveBeenCalledWith(teamMembership);
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

    describe('trackTeamById', () => {
      it('Should return tracked Team primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTeamById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
