export interface INetworkSwitch {
  id?: number;
  interfaceName?: string;
  sshHost?: string;
  sshPort?: number;
  sshKey?: any;
}

export class NetworkSwitch implements INetworkSwitch {
  constructor(public id?: number, public interfaceName?: string, public sshHost?: string, public sshPort?: number, public sshKey?: any) {}
}
