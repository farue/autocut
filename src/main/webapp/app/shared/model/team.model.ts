import { ITeamMembership } from 'app/shared/model/team-membership.model';

export interface ITeam {
  id?: number;
  name?: string;
  teamMemberships?: ITeamMembership[];
}

export class Team implements ITeam {
  constructor(public id?: number, public name?: string, public teamMemberships?: ITeamMembership[]) {}
}
