import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { Workation } from '../../models/workation.model';
import { WorkationTableComponent } from './workation-table.component';

const SAMPLE: Workation[] = [
  { workationId: 'w1', employee: 'Steffen Jacobs', origin: 'Germany', destination: 'United States', start: '2024-01-02', end: '2024-12-31', workingDays: 65, risk: 'HIGH' },
  { workationId: 'w4', employee: 'Andre Fischer', origin: 'Germany', destination: 'Greece', start: '2023-05-22', end: '2023-06-30', workingDays: 50, risk: 'LOW' },
  { workationId: 'w5', employee: 'Ayushi Singh', origin: 'Germany', destination: 'India', start: '2023-03-13', end: '2023-04-30', workingDays: 35, risk: 'NO' }
];

describe('WorkationTableComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WorkationTableComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  function createAndLoad() {
    const fixture = TestBed.createComponent(WorkationTableComponent);
    fixture.detectChanges(); // triggers ngOnInit -> GET
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/workflex/workation`);
    req.flush(SAMPLE);
    fixture.detectChanges();
    return fixture;
  }

  it('loads workations on init', () => {
    const fixture = createAndLoad();
    expect(fixture.componentInstance.workations.length).toBe(3);
    expect(fixture.componentInstance.loading).toBeFalse();
  });

  it('sorts by working days ascending then descending', () => {
    const fixture = createAndLoad();
    const c = fixture.componentInstance;

    c.sortBy('workingDays');
    expect(c.workations.map((w) => w.workingDays)).toEqual([35, 50, 65]);

    c.sortBy('workingDays');
    expect(c.workations.map((w) => w.workingDays)).toEqual([65, 50, 35]);
  });

  it('sorts by risk severity', () => {
    const fixture = createAndLoad();
    const c = fixture.componentInstance;

    c.sortBy('risk');
    expect(c.workations.map((w) => w.risk)).toEqual(['NO', 'LOW', 'HIGH']);
  });

  it('maps risk to label and icon (LOW and NO both read "No risk")', () => {
    const fixture = createAndLoad();
    const c = fixture.componentInstance;

    expect(c.risk('HIGH').label).toBe('High risk');
    expect(c.risk('LOW').label).toBe('No risk');
    expect(c.risk('NO').label).toBe('No risk');
    expect(c.risk('LOW').icon).not.toBe(c.risk('NO').icon);
  });

  it('shows an error message when the request fails', () => {
    const fixture = TestBed.createComponent(WorkationTableComponent);
    fixture.detectChanges();
    httpMock
      .expectOne(`${environment.apiBaseUrl}/workflex/workation`)
      .flush('boom', { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();
    expect(fixture.componentInstance.error).toBeTruthy();
  });
});
