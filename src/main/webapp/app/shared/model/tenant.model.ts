import { IUser } from 'app/core/user/user.model';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';
import { ILease } from 'app/shared/model/lease.model';

export interface ITenant {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  user?: IUser;
  teamMemberships?: ITeamMember[];
  messages?: ITenantCommunication[];
  lease?: ILease;
}

export class Tenant implements ITenant {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public user?: IUser,
    public teamMemberships?: ITeamMember[],
    public messages?: ITenantCommunication[],
    public lease?: ILease
  ) {}
}
