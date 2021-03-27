import { IUser } from 'app/entities/user/user.model';
import { ISecurityPolicy } from 'app/entities/security-policy/security-policy.model';
import { ILease } from 'app/entities/lease/lease.model';

export interface ITenant {
  id?: number;
  firstName?: string;
  lastName?: string;
  pictureIdContentType?: string | null;
  pictureId?: string | null;
  verified?: boolean | null;
  user?: IUser | null;
  securityPolicies?: ISecurityPolicy[] | null;
  lease?: ILease | null;
}

export class Tenant implements ITenant {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public pictureIdContentType?: string | null,
    public pictureId?: string | null,
    public verified?: boolean | null,
    public user?: IUser | null,
    public securityPolicies?: ISecurityPolicy[] | null,
    public lease?: ILease | null
  ) {
    this.verified = this.verified ?? false;
  }
}

export function getTenantIdentifier(tenant: ITenant): number | undefined {
  return tenant.id;
}
