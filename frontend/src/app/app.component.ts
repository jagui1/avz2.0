import { Component, OnInit, inject } from '@angular/core';
import { HelloService } from './hello.service';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  private readonly helloService = inject(HelloService);

  title = 'AVZ 2.0';
  message = 'Loading...';
  error: string | null = null;

  ngOnInit(): void {
    this.helloService.getHello().subscribe({
      next: (response) => {
        this.message = response.message;
        this.error = null;
      },
      error: (err) => {
        this.message = '';
        this.error =
          'Could not reach the backend. Is Spring Boot running on port 8080?';
        console.error(err);
      },
    });
  }
}
