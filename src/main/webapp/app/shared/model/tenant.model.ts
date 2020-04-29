import { IUser } from 'app/core/user/user.model';
import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { ILease } from 'app/shared/model/lease.model';

export interface ITenant {
  id?: number;
  pictureIdContentType?: string;
  pictureId?: any;
  verified?: boolean;
  user?: IUser;
  securityPolicies?: ISecurityPolicy[];
  lease?: ILease;
}

export class Tenant implements ITenant {
  constructor(
    public id?: number,
    public pictureIdContentType?: string,
    public pictureId?: any,
    public verified?: boolean,
    public user?: IUser,
    public securityPolicies?: ISecurityPolicy[],
    public lease?: ILease
  ) {
    this.verified = this.verified || false;
  }
}
