import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import {DATE_FORMAT} from 'app/config/input.constants';
import {TeamRole} from 'app/entities/enumerations/team-role.model';
import {ITeamMembership, TeamMembership} from '../team-membership.model';

import {TeamMembershipService} from './team-membership.service';

describe('TeamMembership Service', () => {
  let service: TeamMembershipService;
  let httpMock: HttpTestingController;
  let elemDefault: ITeamMembership;
  let expectedResult: ITeamMembership | ITeamMembership[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TeamMembershipService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      role: TeamRole.LEAD,
      start: currentDate,
      end: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          start: currentDate.format(DATE_FORMAT),
          end: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a TeamMembership', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          start: currentDate.format(DATE_FORMAT),
          end: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          start: currentDate,
          end: currentDate,
        },
        returnedFromService
      );

      service.create(new TeamMembership()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeamMembership', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          role: 'BBBBBB',
          start: currentDate.format(DATE_FORMAT),
          end: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          start: currentDate,
          end: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeamMembership', () => {
      const patchObject = Object.assign(
        {
          role: 'BBBBBB',
          end: currentDate.format(DATE_FORMAT),
        },
        new TeamMembership()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          start: currentDate,
          end: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeamMembership', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          role: 'BBBBBB',
          start: currentDate.format(DATE_FORMAT),
          end: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          start: currentDate,
          end: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a TeamMembership', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTeamMembershipToCollectionIfMissing', () => {
      it('should add a TeamMembership to an empty array', () => {
        const teamMembership: ITeamMembership = { id: 123 };
        expectedResult = service.addTeamMembershipToCollectionIfMissing([], teamMembership);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamMembership);
      });

      it('should not add a TeamMembership to an array that contains it', () => {
        const teamMembership: ITeamMembership = { id: 123 };
        const teamMembershipCollection: ITeamMembership[] = [
          {
            ...teamMembership,
          },
          { id: 456 },
        ];
        expectedResult = service.addTeamMembershipToCollectionIfMissing(teamMembershipCollection, teamMembership);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeamMembership to an array that doesn't contain it", () => {
        const teamMembership: ITeamMembership = { id: 123 };
        const teamMembershipCollection: ITeamMembership[] = [{ id: 456 }];
        expectedResult = service.addTeamMembershipToCollectionIfMissing(teamMembershipCollection, teamMembership);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamMembership);
      });

      it('should add only unique TeamMembership to an array', () => {
        const teamMembershipArray: ITeamMembership[] = [{ id: 123 }, { id: 456 }, { id: 48109 }];
        const teamMembershipCollection: ITeamMembership[] = [{ id: 123 }];
        expectedResult = service.addTeamMembershipToCollectionIfMissing(teamMembershipCollection, ...teamMembershipArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teamMembership: ITeamMembership = { id: 123 };
        const teamMembership2: ITeamMembership = { id: 456 };
        expectedResult = service.addTeamMembershipToCollectionIfMissing([], teamMembership, teamMembership2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamMembership);
        expect(expectedResult).toContain(teamMembership2);
      });

      it('should accept null and undefined values', () => {
        const teamMembership: ITeamMembership = { id: 123 };
        expectedResult = service.addTeamMembershipToCollectionIfMissing([], null, teamMembership, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamMembership);
      });

      it('should return initial array if no TeamMembership is added', () => {
        const teamMembershipCollection: ITeamMembership[] = [{ id: 123 }];
        expectedResult = service.addTeamMembershipToCollectionIfMissing(teamMembershipCollection, undefined, null);
        expect(expectedResult).toEqual(teamMembershipCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
