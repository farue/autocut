import { ITeamMembership } from 'app/shared/model/team-membership.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { ProtectionUnits } from 'app/shared/model/enumerations/protection-units.model';
import { Access } from 'app/shared/model/enumerations/access.model';

export interface ISecurityPolicy {
  id?: number;
  protectionUnit?: ProtectionUnits;
  access?: Access;
  teamMember?: ITeamMembership;
  tenant?: ITenant;
}

export class SecurityPolicy implements ISecurityPolicy {
  constructor(
    public id?: number,
    public protectionUnit?: ProtectionUnits,
    public access?: Access,
    public teamMember?: ITeamMembership,
    public tenant?: ITenant
  ) {}
}
