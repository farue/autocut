import dayjs from 'dayjs/esm';

export class Registration {
  constructor(
    public login: string,
    public firstName: string,
    public lastName: string,
    public apartment: string,
    public start: dayjs.Dayjs,
    public end: dayjs.Dayjs,
    public email: string,
    public password: string,
    public langKey: string
  ) {}
}
