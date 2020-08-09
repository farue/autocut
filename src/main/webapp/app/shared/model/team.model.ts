import { ITeamMember } from 'app/shared/model/team-member.model';

export interface ITeam {
  id?: number;
  name?: string;
  members?: ITeamMember[];
}

export class Team implements ITeam {
  constructor(public id?: number, public name?: string, public members?: ITeamMember[]) {}
}
