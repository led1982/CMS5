import { expect, test } from '@playwright/test';

test('announcement area is visible on portal home', async ({ page }) => {
  await page.goto('/portal');
  await expect(page.getByRole('heading', { name: '중요 공지' })).toBeVisible();
});
