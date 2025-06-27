-- ---------------------------------------------
-- clinic_locations（最初に挿入）
-- ---------------------------------------------
INSERT IGNORE INTO clinic_locations(id, name, address, phone, created_at)
VALUES
  (1, '中央クリニック', '東京都中央区1-1-1', '03-1234-5678', NOW());

-- ---------------------------------------------
-- users
-- ---------------------------------------------
INSERT IGNORE INTO users (id, username, password_hash, role, full_name, created_at) VALUES
  (100, 'adminUser1' , '$2a$10$EpvF/7YL9cLkFPMnbj40B.0JaYt8tc/53TRoY8N4UbBWmbEUVk7om', 'ROLE_ADMIN' , 'システム管理者', NOW()),
  (101, 'doctorUser1', '$2a$10$C/7yf1UBCsy1DWmCcuS8je9YxhtfkDM9EYWlj98HEu1bj2umKJHn.', 'ROLE_DOCTOR', '鈴木 花子'  , NOW());

-- ---------------------------------------------
-- admins / doctors
-- ---------------------------------------------
INSERT IGNORE INTO admins(id, created_at) VALUES (100, NOW());

INSERT IGNORE INTO doctors(id, specialty, phone, clinic_location_id, created_at)
SELECT id, '心臓内科', '080-1111-0001', 1, NOW()
  FROM users
 WHERE username = 'doctorUser1'
   AND NOT EXISTS (SELECT 1 FROM doctors WHERE id = users.id);
