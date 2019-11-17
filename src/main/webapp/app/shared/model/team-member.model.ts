import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { ITeam } from 'app/shared/model/team.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { TeamRole } from 'app/shared/model/enumerations/team-role.model';

export interface ITeamMember {
  id?: number;
  role?: TeamRole;
  securityPolicies?: ISecurityPolicy[];
  team?: ITeam;
  tenant?: ITenant;
}

export class TeamMember implements ITeamMember {
  constructor(
    public id?: number,
    public role?: TeamRole,
    public securityPolicies?: ISecurityPolicy[],
    public team?: ITeam,
    public tenant?: ITenant
  ) {}
}
