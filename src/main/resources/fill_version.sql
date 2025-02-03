use market
UPDATE inventory
SET version = 0
WHERE version IS NULL;

UPDATE user
SET version = 0
WHERE version IS NULL;

UPDATE market
SET version = 0
WHERE version IS NULL;