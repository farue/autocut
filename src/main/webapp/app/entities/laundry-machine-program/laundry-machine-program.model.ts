import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';

export interface ILaundryMachineProgram {
  id?: number;
  name?: string;
  subprogram?: string | null;
  time?: number;
  spin?: number | null;
  preWash?: boolean | null;
  protect?: boolean | null;
  laundryMachine?: ILaundryMachine | null;
}

export class LaundryMachineProgram implements ILaundryMachineProgram {
  constructor(
    public id?: number,
    public name?: string,
    public subprogram?: string | null,
    public time?: number,
    public spin?: number | null,
    public preWash?: boolean | null,
    public protect?: boolean | null,
    public laundryMachine?: ILaundryMachine | null
  ) {
    this.preWash = this.preWash ?? false;
    this.protect = this.protect ?? false;
  }
}

export function getLaundryMachineProgramIdentifier(laundryMachineProgram: ILaundryMachineProgram): number | undefined {
  return laundryMachineProgram.id;
}
