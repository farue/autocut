import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';
import {Lightbox} from 'ngx-lightbox';

import {LoginModalService} from 'app/core/login/login-modal.service';
import {AccountService} from 'app/core/auth/account.service';
import {Account} from 'app/core/user/account.model';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account;
  authSubscription: Subscription;
  modalRef: NgbModalRef;

  images: any[];

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager,
    private _lightbox: Lightbox
  ) {}

  ngOnInit() {
    this.accountService.identity().subscribe((account: Account) => {
      this.account = account;
    });
    this.registerAuthenticationSuccess();
    this.images = [];
    this.images.push({source: '/content/images/farue0.jpg', src: '/content/images/farue0.jpg'});
    this.images.push({source: '/content/images/farue1.jpg', src: '/content/images/farue1.jpg'});
    this.images.push({source: '/content/images/farue2.jpg', src: '/content/images/farue2.jpg'});
    this.images.push({source: '/content/images/farue3.jpg', src: '/content/images/farue3.jpg'});
    this.images.push({source: '/content/images/farue4.jpg', src: '/content/images/farue4.jpg'});
    this.images.push({source: '/content/images/farue5.jpg', src: '/content/images/farue5.jpg'});
    this.images.push({source: '/content/images/farue6.jpg', src: '/content/images/farue6.jpg'});
    this.images.push({source: '/content/images/farue7.jpg', src: '/content/images/farue7.jpg'});
    this.images.push({source: '/content/images/farue8.jpg', src: '/content/images/farue8.jpg'});
    this.images.push({source: '/content/images/farueeingang1.jpg', src: '/content/images/farueeingang1.jpg'});
    this.images.push({source: '/content/images/farueeingang2.jpg', src: '/content/images/farueeingang2.jpg'});
    this.images.push({source: '/content/images/farueeingang3.jpg', src: '/content/images/farueeingang3.jpg'});
  }

  registerAuthenticationSuccess() {
    this.authSubscription = this.eventManager.subscribe('authenticationSuccess', message => {
      this.accountService.identity().subscribe(account => {
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

  ngOnDestroy() {
    if (this.authSubscription) {
      this.eventManager.destroy(this.authSubscription);
    }
  }

  onImageClicked($event: any) {
    this._lightbox.open(this.images, $event.index);
  }
}
