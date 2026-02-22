-- Minimal development seed data for product_compare
-- Run this in MySQL before testing:
--   mysql -u root -p product_compare < sample-data.sql

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM feature_value WHERE product_id IN (1, 2) OR feature_id IN (1, 2);
DELETE FROM feature WHERE id IN (1, 2);
DELETE FROM product WHERE id IN (1, 2);
DELETE FROM user_workspace WHERE id = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO user_workspace (id, username, password_hash, created_at)
VALUES (1, 'demo_user', NULL, NOW());

INSERT INTO product (id, name, category, price, image_url, workspace_id)
VALUES
  (1, 'Phone Alpha', 'Smartphone', 49999.00, 'https://example.com/alpha.jpg', 1),
  (2, 'Phone Beta', 'Smartphone', 54999.00, 'https://example.com/beta.jpg', 1);

INSERT INTO feature (id, name, importance, workspace_id)
VALUES
  (1, 'RAM', 1, 1),
  (2, 'Battery', 1, 1);

INSERT INTO feature_value (product_id, feature_id, value, version, updated_at)
VALUES
  (1, 1, '8 GB', 1, NOW()),
  (1, 2, '5000 mAh', 1, NOW()),
  (2, 1, '12 GB', 1, NOW()),
  (2, 2, '4500 mAh', 1, NOW());
