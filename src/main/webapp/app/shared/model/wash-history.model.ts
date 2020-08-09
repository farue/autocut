import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';
import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';
import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { WashHistoryStatus } from 'app/shared/model/enumerations/wash-history-status.model';

export interface IWashHistory {
  id?: number;
  usingDate?: Moment;
  reservationDate?: Moment;
  lastModifiedDate?: Moment;
  status?: WashHistoryStatus;
  reservationTenant?: ITenant;
  usingTenant?: ITenant;
  machine?: ILaundryMachine;
  program?: ILaundryMachineProgram;
}

export class WashHistory implements IWashHistory {
  constructor(
    public id?: number,
    public usingDate?: Moment,
    public reservationDate?: Moment,
    public lastModifiedDate?: Moment,
    public status?: WashHistoryStatus,
    public reservationTenant?: ITenant,
    public usingTenant?: ITenant,
    public machine?: ILaundryMachine,
    public program?: ILaundryMachineProgram
  ) {}
}
