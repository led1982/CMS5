import { expect, test } from '@playwright/test';

test('portal search route renders filters and result area', async ({ page }) => {
  await page.goto('/portal/search?q=휴가');
  await expect(page.getByLabel('검색어')).toBeVisible();
  await expect(page.getByRole('heading', { name: '검색 결과' })).toBeVisible();
});
