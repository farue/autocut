import { LaundryMachineType } from 'app/entities/enumerations/laundry-machine-type.model';
import dayjs from 'dayjs/esm';

export interface Machine {
  id: number;
  name: string;
  type: LaundryMachineType;
  enabled: boolean;
  positionX: number;
  positionY: number;
  inUseUntil?: dayjs.Dayjs;
}

export interface Program {
  id: number;
  name: string;
  subprogram?: string | null;
  spin?: number | null;
  preWash?: boolean | null;
  protect?: boolean | null;
  time: number;
}
