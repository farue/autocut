import { Moment } from 'moment';
import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { ITeam } from 'app/shared/model/team.model';
import { TeamRole } from 'app/shared/model/enumerations/team-role.model';

export interface ITeamMembership {
  id?: number;
  role?: TeamRole;
  start?: Moment;
  end?: Moment;
  securityPolicies?: ISecurityPolicy[];
  tenant?: ITenant;
  team?: ITeam;
}

export class TeamMembership implements ITeamMembership {
  constructor(
    public id?: number,
    public role?: TeamRole,
    public start?: Moment,
    public end?: Moment,
    public securityPolicies?: ISecurityPolicy[],
    public tenant?: ITenant,
    public team?: ITeam
  ) {}
}
