export type Risk = 'HIGH' | 'LOW' | 'NO';

/** A workation as returned by GET /workflex/workation. */
export interface Workation {
  workationId: string;
  employee: string;
  origin: string;
  destination: string;
  start: string;
  end: string;
  workingDays: number;
  risk: Risk;
}
