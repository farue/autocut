import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { LaundryMachineType } from 'app/shared/model/enumerations/laundry-machine-type.model';

export interface ILaundryMachine {
  id?: number;
  identifier?: string;
  name?: string;
  type?: LaundryMachineType;
  enabled?: boolean;
  programs?: ILaundryMachineProgram[];
}

export class LaundryMachine implements ILaundryMachine {
  constructor(
    public id?: number,
    public identifier?: string,
    public name?: string,
    public type?: LaundryMachineType,
    public enabled?: boolean,
    public programs?: ILaundryMachineProgram[]
  ) {
    this.enabled = this.enabled || false;
  }
}
