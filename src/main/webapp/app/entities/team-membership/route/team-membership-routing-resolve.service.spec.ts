import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITeamMembership, TeamMembership } from '../team-membership.model';
import { TeamMembershipService } from '../service/team-membership.service';

import { TeamMembershipRoutingResolveService } from './team-membership-routing-resolve.service';

describe('TeamMembership routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TeamMembershipRoutingResolveService;
  let service: TeamMembershipService;
  let resultTeamMembership: ITeamMembership | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(TeamMembershipRoutingResolveService);
    service = TestBed.inject(TeamMembershipService);
    resultTeamMembership = undefined;
  });

  describe('resolve', () => {
    it('should return ITeamMembership returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTeamMembership = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTeamMembership).toEqual({ id: 123 });
    });

    it('should return new ITeamMembership if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTeamMembership = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTeamMembership).toEqual(new TeamMembership());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TeamMembership })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTeamMembership = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTeamMembership).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
