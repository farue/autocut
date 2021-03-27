import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';

export interface ITeam {
  id?: number;
  name?: string;
  teamMemberships?: ITeamMembership[] | null;
}

export class Team implements ITeam {
  constructor(public id?: number, public name?: string, public teamMemberships?: ITeamMembership[] | null) {}
}

export function getTeamIdentifier(team: ITeam): number | undefined {
  return team.id;
}
