import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';

export interface ITeam {
  id?: number;
  name?: string;
  email?: string | null;
  teamMemberships?: ITeamMembership[] | null;
}

export class Team implements ITeam {
  constructor(public id?: number, public name?: string, public email?: string | null, public teamMemberships?: ITeamMembership[] | null) {}
}

export function getTeamIdentifier(team: ITeam): number | undefined {
  return team.id;
}
