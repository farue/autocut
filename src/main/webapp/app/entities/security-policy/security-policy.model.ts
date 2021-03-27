import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { ProtectionUnits } from 'app/entities/enumerations/protection-units.model';
import { Access } from 'app/entities/enumerations/access.model';

export interface ISecurityPolicy {
  id?: number;
  protectionUnit?: ProtectionUnits;
  access?: Access;
  teamMember?: ITeamMembership | null;
  tenant?: ITenant | null;
}

export class SecurityPolicy implements ISecurityPolicy {
  constructor(
    public id?: number,
    public protectionUnit?: ProtectionUnits,
    public access?: Access,
    public teamMember?: ITeamMembership | null,
    public tenant?: ITenant | null
  ) {}
}

export function getSecurityPolicyIdentifier(securityPolicy: ISecurityPolicy): number | undefined {
  return securityPolicy.id;
}
