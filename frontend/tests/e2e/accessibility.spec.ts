import { expect, test } from '@playwright/test';

test('primary application landmarks are present', async ({ page }) => {
  await page.goto('/portal');
  await expect(page.getByLabel('주요 메뉴')).toBeVisible();
  await expect(page.getByRole('main')).toBeVisible();
});
