import dayjs from 'dayjs/esm';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { ILaundryMachineProgram } from 'app/entities/laundry-machine-program/laundry-machine-program.model';
import { WashHistoryStatus } from 'app/entities/enumerations/wash-history-status.model';

export interface IWashHistory {
  id?: number;
  usingDate?: dayjs.Dayjs | null;
  reservationDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  status?: WashHistoryStatus | null;
  reservationTenant?: ITenant | null;
  usingTenant?: ITenant | null;
  machine?: ILaundryMachine | null;
  program?: ILaundryMachineProgram | null;
}

export class WashHistory implements IWashHistory {
  constructor(
    public id?: number,
    public usingDate?: dayjs.Dayjs | null,
    public reservationDate?: dayjs.Dayjs | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public status?: WashHistoryStatus | null,
    public reservationTenant?: ITenant | null,
    public usingTenant?: ITenant | null,
    public machine?: ILaundryMachine | null,
    public program?: ILaundryMachineProgram | null
  ) {}
}

export function getWashHistoryIdentifier(washHistory: IWashHistory): number | undefined {
  return washHistory.id;
}
