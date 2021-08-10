import { IBroadcastMessage } from 'app/entities/broadcast-message/broadcast-message.model';

export interface IBroadcastMessageText {
  id?: number;
  langKey?: string;
  text?: string;
  message?: IBroadcastMessage;
}

export class BroadcastMessageText implements IBroadcastMessageText {
  constructor(public id?: number, public langKey?: string, public text?: string, public message?: IBroadcastMessage) {}
}

export function getBroadcastMessageTextIdentifier(broadcastMessageText: IBroadcastMessageText): number | undefined {
  return broadcastMessageText.id;
}
