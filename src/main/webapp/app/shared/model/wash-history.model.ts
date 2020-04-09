import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';
import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';
import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

export interface IWashHistory {
  id?: number;
  date?: Moment;
  reservation?: Moment;
  tenant?: ITenant;
  machine?: ILaundryMachine;
  program?: ILaundryMachineProgram;
}

export class WashHistory implements IWashHistory {
  constructor(
    public id?: number,
    public date?: Moment,
    public reservation?: Moment,
    public tenant?: ITenant,
    public machine?: ILaundryMachine,
    public program?: ILaundryMachineProgram
  ) {}
}
