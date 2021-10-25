export interface INetworkSwitch {
  id?: number;
  interfaceName?: string;
  sshHost?: string;
}

export class NetworkSwitch implements INetworkSwitch {
  constructor(public id?: number, public interfaceName?: string, public sshHost?: string) {}
}

export function getNetworkSwitchIdentifier(networkSwitch: INetworkSwitch): number | undefined {
  return networkSwitch.id;
}
