import { expect, test } from '@playwright/test';

test('editor can reach content workflow screen', async ({ page }) => {
  await page.goto('/portal');
  await page.getByLabel('프로필').selectOption('editor');
  await page.getByRole('link', { name: /CMS/ }).click();
  await expect(page.getByRole('heading', { name: 'CMS 콘텐츠 목록' })).toBeVisible();
});
