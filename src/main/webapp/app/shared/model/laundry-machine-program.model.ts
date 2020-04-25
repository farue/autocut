import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';

export interface ILaundryMachineProgram {
  id?: number;
  name?: string;
  subprogram?: string;
  time?: number;
  spin?: number;
  preWash?: boolean;
  protect?: boolean;
  laundryMachine?: ILaundryMachine;
}

export class LaundryMachineProgram implements ILaundryMachineProgram {
  constructor(
    public id?: number,
    public name?: string,
    public subprogram?: string,
    public time?: number,
    public spin?: number,
    public preWash?: boolean,
    public protect?: boolean,
    public laundryMachine?: ILaundryMachine
  ) {
    this.preWash = this.preWash || false;
    this.protect = this.protect || false;
  }
}
