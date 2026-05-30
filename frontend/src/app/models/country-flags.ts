/**
 * Maps country names (as they appear in the CSV) to ISO 3166-1 alpha-2 codes,
 * used to build flag image URLs from flagcdn.com.
 *
 * Extend this map as more destinations are added to the dataset.
 */
const COUNTRY_CODES: Record<string, string> = {
  'Germany': 'de',
  'United States': 'us',
  'Ukraine': 'ua',
  'Belgium': 'be',
  'Spain': 'es',
  'Greece': 'gr',
  'India': 'in',
  // A few extra common ones for robustness:
  'France': 'fr',
  'Italy': 'it',
  'United Kingdom': 'gb',
  'Netherlands': 'nl',
  'Portugal': 'pt',
  'Poland': 'pl',
  'Austria': 'at',
  'Switzerland': 'ch'
};

/** Returns the flagcdn URL for a country, or undefined if the country is unknown. */
export function flagUrl(country: string): string | undefined {
  const code = COUNTRY_CODES[country];
  return code ? `https://flagcdn.com/24x18/${code}.png` : undefined;
}
