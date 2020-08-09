import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { ITeam } from 'app/shared/model/team.model';
import { IActivity } from 'app/shared/model/activity.model';
import { TeamRole } from 'app/shared/model/enumerations/team-role.model';

export interface ITeamMember {
  id?: number;
  role?: TeamRole;
  securityPolicies?: ISecurityPolicy[];
  tenant?: ITenant;
  team?: ITeam;
  activity?: IActivity;
}

export class TeamMember implements ITeamMember {
  constructor(
    public id?: number,
    public role?: TeamRole,
    public securityPolicies?: ISecurityPolicy[],
    public tenant?: ITenant,
    public team?: ITeam,
    public activity?: IActivity
  ) {}
}
