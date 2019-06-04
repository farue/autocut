import {Component, OnInit} from '@angular/core';
import {NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {Account, AccountService, LoginModalService} from 'app/core';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
  account: Account;
  modalRef: NgbModalRef;

  images: any[];

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager
  ) {}

  ngOnInit() {
    this.accountService.identity().then((account: Account) => {
      this.account = account;
    });
    this.registerAuthenticationSuccess();

    this.images = [];
    this.images.push({source: '/content/images/farue0.jpg'});
    this.images.push({source: '/content/images/farue1.jpg'});
    this.images.push({source: '/content/images/farue2.jpg'});
    this.images.push({source: '/content/images/farue3.jpg'});
    this.images.push({source: '/content/images/farue4.jpg'});
    this.images.push({source: '/content/images/farue5.jpg'});
    this.images.push({source: '/content/images/farue6.jpg'});
    this.images.push({source: '/content/images/farue7.jpg'});
    this.images.push({source: '/content/images/farue8.jpg'});
    this.images.push({source: '/content/images/farueeingang1.jpg'});
    this.images.push({source: '/content/images/farueeingang2.jpg'});
    this.images.push({source: '/content/images/farueeingang3.jpg'});
  }

  registerAuthenticationSuccess() {
    this.eventManager.subscribe('authenticationSuccess', message => {
      this.accountService.identity().then(account => {
        this.account = account;
      });
    });
  }

  isAuthenticated() {
    return this.accountService.isAuthenticated();
  }

  login() {
    this.modalRef = this.loginModalService.open();
  }

  onImageClicked($event: any) {
    window.open($event.image.source, '_blank');
  }
}
