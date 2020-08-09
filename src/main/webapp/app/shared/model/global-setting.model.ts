export interface IGlobalSetting {
  id?: number;
  key?: string;
  value?: string;
  valueType?: string;
}

export class GlobalSetting implements IGlobalSetting {
  constructor(public id?: number, public key?: string, public value?: string, public valueType?: string) {}
}
