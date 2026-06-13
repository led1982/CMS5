import { expect, test } from '@playwright/test';

test('admin can reach governance navigation', async ({ page }) => {
  await page.goto('/portal');
  await page.getByLabel('프로필').selectOption('admin');
  await page.getByRole('link', { name: /권한/ }).click();
  await expect(page.getByRole('heading', { name: '사용자·역할 관리' })).toBeVisible();
});
