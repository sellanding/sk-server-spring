-- Migration: Remove unused box_count from usage_counters
-- Date: 2026-03-17

ALTER TABLE usage_counters DROP COLUMN IF EXISTS box_count;
