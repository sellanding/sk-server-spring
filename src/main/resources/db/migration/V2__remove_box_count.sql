-- V2__remove_box_count.sql
-- Remove unused box_count from usage_counters (carried over from Go server migration)

-- Already handled in V1, but added for version history consistency.
-- This represents an actual change that occurred in the DB history.
ALTER TABLE usage_counters DROP COLUMN IF EXISTS box_count;
