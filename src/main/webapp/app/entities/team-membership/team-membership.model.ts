import dayjs from 'dayjs/esm';
import { ISecurityPolicy } from 'app/entities/security-policy/security-policy.model';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { ITeam } from 'app/entities/team/team.model';
import { TeamRole } from 'app/entities/enumerations/team-role.model';

export interface ITeamMembership {
  id?: number;
  role?: TeamRole | null;
  start?: dayjs.Dayjs | null;
  end?: dayjs.Dayjs | null;
  securityPolicies?: ISecurityPolicy[] | null;
  tenant?: ITenant | null;
  team?: ITeam;
}

export class TeamMembership implements ITeamMembership {
  constructor(
    public id?: number,
    public role?: TeamRole | null,
    public start?: dayjs.Dayjs | null,
    public end?: dayjs.Dayjs | null,
    public securityPolicies?: ISecurityPolicy[] | null,
    public tenant?: ITenant | null,
    public team?: ITeam
  ) {}
}

export function getTeamMembershipIdentifier(teamMembership: ITeamMembership): number | undefined {
  return teamMembership.id;
}
