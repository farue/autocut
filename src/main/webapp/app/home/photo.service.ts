import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Photo } from './photo.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PhotoData } from 'app/home/photo-data.model';

@Injectable({ providedIn: 'root' })
export class PhotoService {
  img: any;

  constructor(private http: HttpClient) {}

  getPhotoContainers(): Observable<Photo[]> {
    // this.http.get('content/images/galleria1.jpg', { responseType: 'blob' }).subscribe(result => {
    //   this.img = result;
    // });
    return this.http.get<PhotoData>('content/data/photos.json').pipe(map(result => result.data));
  }

  getPhotoContainers2(): Promise<Photo[]> {
    return this.http
      .get<any>('content/data/photos.json')
      .toPromise()
      .then(res => res.data as Photo[]);
  }

  // getPhotos(): void {
  //   this.getPhotoContainers().pipe(map(imageContainers => {
  //     return imageContainers.map(imageContainer => {
  //       forkJoin(
  //
  //       )
  //       .subscribe()
  //     });
  //   }))
  //   this.getPhotoContainers().subscribe(image => {
  //     for (const img of image) {
  //       if (img.previewImageSrc === undefined || img.thumbnailImageSrc === undefined) {
  //         throw new Error('Image location missing.')
  //       }
  //       this.http.get<any>(img.previewImageSrc).subscribe(previewImg => {
  //
  //       })
  //     }
  //   })
  // }
}
