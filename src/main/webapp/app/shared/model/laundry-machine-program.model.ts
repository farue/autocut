import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';

export interface ILaundryMachineProgram {
  id?: number;
  name?: string;
  time?: number;
  temperature?: number;
  spin?: number;
  preWash?: boolean;
  protect?: boolean;
  shortCycle?: boolean;
  wrinkle?: boolean;
  laundryMachine?: ILaundryMachine;
}

export class LaundryMachineProgram implements ILaundryMachineProgram {
  constructor(
    public id?: number,
    public name?: string,
    public time?: number,
    public temperature?: number,
    public spin?: number,
    public preWash?: boolean,
    public protect?: boolean,
    public shortCycle?: boolean,
    public wrinkle?: boolean,
    public laundryMachine?: ILaundryMachine
  ) {
    this.preWash = this.preWash || false;
    this.protect = this.protect || false;
    this.shortCycle = this.shortCycle || false;
    this.wrinkle = this.wrinkle || false;
  }
}
