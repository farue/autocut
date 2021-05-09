import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/auth/account.model';
import { Photo } from 'app/home/photo.model';
import { AccountService } from 'app/core/auth/account.service';
import { PhotoService } from 'app/home/photo.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { OnDestroy } from '@angular/core';

@Component({
  selector: 'jhi-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss'],
})
export class WelcomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;

  images: Photo[] = [];

  responsiveOptions: any[] = [
    {
      breakpoint: '1024px',
      numVisible: 5,
    },
    {
      breakpoint: '768px',
      numVisible: 3,
    },
    {
      breakpoint: '560px',
      numVisible: 1,
    },
  ];

  constructor(private accountService: AccountService, private router: Router, private photoService: PhotoService) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.photoService.getPhotoContainers().subscribe(images => (this.images = images));
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  redirectToWpWebsite(): void {
    window.location.href = 'https://www.wp.farue.rwth-aachen.de';
  }
}
