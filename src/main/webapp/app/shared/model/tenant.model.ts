import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { IActivity } from 'app/shared/model/activity.model';
import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';
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
  teamMemberships?: ITeamMember[];
  securityPolicies?: ISecurityPolicy[];
  activties?: IActivity[];
  messages?: ITenantCommunication[];
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
    public teamMemberships?: ITeamMember[],
    public securityPolicies?: ISecurityPolicy[],
    public activties?: IActivity[],
    public messages?: ITenantCommunication[],
    public lease?: ILease
  ) {}
}
