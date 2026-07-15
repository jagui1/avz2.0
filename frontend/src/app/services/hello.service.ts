import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HelloResponse {
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class HelloService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/hello';

  getHello(): Observable<HelloResponse> {
    return this.http.get<HelloResponse>(this.apiUrl);
  }
}
