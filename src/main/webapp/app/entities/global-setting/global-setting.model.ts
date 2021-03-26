export interface IGlobalSetting {
  id?: number;
  key?: string | null;
  value?: string | null;
  valueType?: string | null;
}

export class GlobalSetting implements IGlobalSetting {
  constructor(public id?: number, public key?: string | null, public value?: string | null, public valueType?: string | null) {}
}

export function getGlobalSettingIdentifier(globalSetting: IGlobalSetting): number | undefined {
  return globalSetting.id;
}
