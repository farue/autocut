import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';
import { ITeamMembership } from 'app/shared/model/team-membership.model';
import { SemesterTerms } from 'app/shared/model/enumerations/semester-terms.model';

export interface IActivity {
  id?: number;
  year?: number;
  term?: SemesterTerms;
  start?: Moment;
  end?: Moment;
  description?: string;
  discount?: boolean;
  stwActivity?: boolean;
  tenant?: ITenant;
  teamMembership?: ITeamMembership;
}

export class Activity implements IActivity {
  constructor(
    public id?: number,
    public year?: number,
    public term?: SemesterTerms,
    public start?: Moment,
    public end?: Moment,
    public description?: string,
    public discount?: boolean,
    public stwActivity?: boolean,
    public tenant?: ITenant,
    public teamMembership?: ITeamMembership
  ) {
    this.discount = this.discount || false;
    this.stwActivity = this.stwActivity || false;
  }
}
