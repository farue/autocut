import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { ILease } from 'app/shared/model/lease.model';

export interface ITenant {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  user?: IUser;
  securityPolicies?: ISecurityPolicy[];
  lease?: ILease;
}

export class Tenant implements ITenant {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public user?: IUser,
    public securityPolicies?: ISecurityPolicy[],
    public lease?: ILease
  ) {}
}
