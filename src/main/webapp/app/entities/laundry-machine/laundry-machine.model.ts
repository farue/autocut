import { ILaundryMachineProgram } from 'app/entities/laundry-machine-program/laundry-machine-program.model';
import { LaundryMachineType } from 'app/entities/enumerations/laundry-machine-type.model';

export interface ILaundryMachine {
  id?: number;
  identifier?: string;
  name?: string;
  type?: LaundryMachineType;
  enabled?: boolean;
  positionX?: number;
  positionY?: number;
  programs?: ILaundryMachineProgram[] | null;
}

export class LaundryMachine implements ILaundryMachine {
  constructor(
    public id?: number,
    public identifier?: string,
    public name?: string,
    public type?: LaundryMachineType,
    public enabled?: boolean,
    public positionX?: number,
    public positionY?: number,
    public programs?: ILaundryMachineProgram[] | null
  ) {
    this.enabled = this.enabled ?? false;
  }
}

export function getLaundryMachineIdentifier(laundryMachine: ILaundryMachine): number | undefined {
  return laundryMachine.id;
}
