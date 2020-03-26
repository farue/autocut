import { Moment } from 'moment';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { SemesterTerms } from 'app/shared/model/enumerations/semester-terms.model';

export interface IActivity {
  id?: number;
  year?: number;
  term?: SemesterTerms;
  startDate?: Moment;
  endDate?: Moment;
  description?: string;
  discount?: boolean;
  stwActivity?: boolean;
  teamMembers?: ITeamMember[];
  tenant?: ITenant;
}

export class Activity implements IActivity {
  constructor(
    public id?: number,
    public year?: number,
    public term?: SemesterTerms,
    public startDate?: Moment,
    public endDate?: Moment,
    public description?: string,
    public discount?: boolean,
    public stwActivity?: boolean,
    public teamMembers?: ITeamMember[],
    public tenant?: ITenant
  ) {
    this.discount = this.discount || false;
    this.stwActivity = this.stwActivity || false;
  }
}
