export interface ILaundryProgram {
  id?: number;
  name?: string;
  subprogram?: string | null;
  spin?: number | null;
  preWash?: boolean | null;
  protect?: boolean | null;
}

export class LaundryProgram implements ILaundryProgram {
  constructor(
    public id?: number,
    public name?: string,
    public subprogram?: string | null,
    public spin?: number | null,
    public preWash?: boolean | null,
    public protect?: boolean | null
  ) {
    this.preWash = this.preWash ?? false;
    this.protect = this.protect ?? false;
  }
}

export function getLaundryProgramIdentifier(laundryProgram: ILaundryProgram): number | undefined {
  return laundryProgram.id;
}
