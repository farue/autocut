export interface IBankAccount {
  id?: number;
  name?: string;
  bic?: string;
  iban?: string;
}

export class BankAccount implements IBankAccount {
  constructor(public id?: number, public name?: string, public bic?: string, public iban?: string) {}
}
