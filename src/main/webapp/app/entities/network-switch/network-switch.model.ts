export interface INetworkSwitch {
  id?: number;
  interfaceName?: string;
  sshHost?: string;
  sshPort?: number;
  sshKey?: string;
}

export class NetworkSwitch implements INetworkSwitch {
  constructor(
    public id?: number,
    public interfaceName?: string,
    public sshHost?: string,
    public sshPort?: number,
    public sshKey?: string
  ) {}
}

export function getNetworkSwitchIdentifier(networkSwitch: INetworkSwitch): number | undefined {
  return networkSwitch.id;
}
