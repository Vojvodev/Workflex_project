import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { flagUrl } from '../../models/country-flags';
import { Risk, Workation } from '../../models/workation.model';
import { WorkationService } from '../../services/workation.service';

type SortableColumn =
  | 'employee'
  | 'origin'
  | 'destination'
  | 'start'
  | 'end'
  | 'workingDays'
  | 'risk';

type SortDirection = 'asc' | 'desc';

interface RiskPresentation {
  label: string;
  icon: string;
  cssClass: string;
}

/** Ordering used when sorting by the risk column (higher = more severe). */
const RISK_RANK: Record<Risk, number> = { HIGH: 2, LOW: 1, NO: 0 };

const RISK_PRESENTATION: Record<Risk, RiskPresentation> = {
  HIGH: { label: 'High risk', icon: 'assets/red-risk.svg', cssClass: 'risk-high' },
  LOW: { label: 'No risk', icon: 'assets/yellow-risk.svg', cssClass: 'risk-low' },
  NO: { label: 'No risk', icon: 'assets/green-risk.svg', cssClass: 'risk-no' }
};

@Component({
  selector: 'app-workation-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workation-table.component.html',
  styleUrl: './workation-table.component.scss'
})
export class WorkationTableComponent implements OnInit {
  workations: Workation[] = [];
  loading = true;
  error: string | null = null;

  sortColumn: SortableColumn = 'employee';
  sortDirection: SortDirection = 'asc';

  readonly columns: { key: SortableColumn; label: string }[] = [
    { key: 'employee', label: 'Employee' },
    { key: 'origin', label: 'Origin' },
    { key: 'destination', label: 'Destination' },
    { key: 'start', label: 'Start' },
    { key: 'end', label: 'End' },
    { key: 'workingDays', label: 'Working days' },
    { key: 'risk', label: 'Risk' }
  ];

  constructor(private readonly workationService: WorkationService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.workationService.getWorkations().subscribe({
      next: (data) => {
        this.workations = data;
        this.applySort();
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load workations. Is the backend running on http://localhost:8080?';
        this.loading = false;
      }
    });
  }

  sortBy(column: SortableColumn): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySort();
  }

  private applySort(): void {
    const dir = this.sortDirection === 'asc' ? 1 : -1;
    const col = this.sortColumn;
    this.workations = [...this.workations].sort((a, b) => dir * this.compare(a, b, col));
  }

  private compare(a: Workation, b: Workation, col: SortableColumn): number {
    switch (col) {
      case 'workingDays':
        return a.workingDays - b.workingDays;
      case 'risk':
        return RISK_RANK[a.risk] - RISK_RANK[b.risk];
      case 'start':
      case 'end':
        // ISO date strings compare chronologically as plain strings.
        return a[col].localeCompare(b[col]);
      default:
        return a[col].localeCompare(b[col]);
    }
  }

  sortIndicator(column: SortableColumn): 'asc' | 'desc' | 'none' {
    return this.sortColumn === column ? this.sortDirection : 'none';
  }

  flag(country: string): string | undefined {
    return flagUrl(country);
  }

  risk(value: Risk): RiskPresentation {
    return RISK_PRESENTATION[value];
  }

  trackById(_index: number, w: Workation): string {
    return w.workationId;
  }
}
