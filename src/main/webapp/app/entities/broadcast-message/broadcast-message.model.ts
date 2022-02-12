import dayjs from 'dayjs/esm';
import { IBroadcastMessageText } from 'app/entities/broadcast-message-text/broadcast-message-text.model';
import { BroadcastMessageType } from 'app/entities/enumerations/broadcast-message-type.model';

export interface IBroadcastMessage {
  id?: number;
  type?: BroadcastMessageType;
  start?: dayjs.Dayjs | null;
  end?: dayjs.Dayjs | null;
  usersOnly?: boolean | null;
  dismissible?: boolean | null;
  messageTexts?: IBroadcastMessageText[] | null;
}

export class BroadcastMessage implements IBroadcastMessage {
  constructor(
    public id?: number,
    public type?: BroadcastMessageType,
    public start?: dayjs.Dayjs | null,
    public end?: dayjs.Dayjs | null,
    public usersOnly?: boolean | null,
    public dismissible?: boolean | null,
    public messageTexts?: IBroadcastMessageText[] | null
  ) {
    this.usersOnly = this.usersOnly ?? false;
    this.dismissible = this.dismissible ?? false;
  }
}

export function getBroadcastMessageIdentifier(broadcastMessage: IBroadcastMessage): number | undefined {
  return broadcastMessage.id;
}
