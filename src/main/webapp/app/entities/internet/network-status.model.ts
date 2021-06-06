import * as dayjs from 'dayjs';

export interface NetworkStatus {
  networkSwitchId: number;
  port: string;
  status: string;
  speed: number;
  maxPossibleSpeed: number;
  lastUpdate: dayjs.Dayjs;
}
