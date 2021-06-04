import { ILaundryProgram } from 'app/entities/laundry-program/laundry-program.model';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';

export interface ILaundryMachineProgram {
  id?: number;
  time?: number;
  program?: ILaundryProgram;
  machine?: ILaundryMachine;
}

export class LaundryMachineProgram implements ILaundryMachineProgram {
  constructor(public id?: number, public time?: number, public program?: ILaundryProgram, public machine?: ILaundryMachine) {}
}

export function getLaundryMachineProgramIdentifier(laundryMachineProgram: ILaundryMachineProgram): number | undefined {
  return laundryMachineProgram.id;
}
