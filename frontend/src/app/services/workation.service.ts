import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Workation } from '../models/workation.model';

@Injectable({ providedIn: 'root' })
export class WorkationService {
  private readonly baseUrl = `${environment.apiBaseUrl}/workflex/workation`;

  constructor(private readonly http: HttpClient) {}

  /** Lists all workations currently in the system. */
  getWorkations(): Observable<Workation[]> {
    return this.http.get<Workation[]>(this.baseUrl);
  }

  /** Triggers a (re)import of the bundled CSV on the backend. */
  importCsv(): Observable<{ imported: number; source: string }> {
    return this.http.post<{ imported: number; source: string }>(`${this.baseUrl}/import`, {});
  }
}
