import dayjs from 'dayjs/esm';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';
import { SemesterTerms } from 'app/entities/enumerations/semester-terms.model';

export interface IActivity {
  id?: number;
  year?: number;
  term?: SemesterTerms;
  start?: dayjs.Dayjs | null;
  end?: dayjs.Dayjs | null;
  description?: string | null;
  discount?: boolean | null;
  stwActivity?: boolean | null;
  tenant?: ITenant | null;
  teamMembership?: ITeamMembership | null;
}

export class Activity implements IActivity {
  constructor(
    public id?: number,
    public year?: number,
    public term?: SemesterTerms,
    public start?: dayjs.Dayjs | null,
    public end?: dayjs.Dayjs | null,
    public description?: string | null,
    public discount?: boolean | null,
    public stwActivity?: boolean | null,
    public tenant?: ITenant | null,
    public teamMembership?: ITeamMembership | null
  ) {
    this.discount = this.discount ?? false;
    this.stwActivity = this.stwActivity ?? false;
  }
}

export function getActivityIdentifier(activity: IActivity): number | undefined {
  return activity.id;
}
