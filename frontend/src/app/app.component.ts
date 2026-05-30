import { Component } from '@angular/core';
import { WorkationTableComponent } from './components/workation-table/workation-table.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [WorkationTableComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'WorkFlex – Workations';
}
