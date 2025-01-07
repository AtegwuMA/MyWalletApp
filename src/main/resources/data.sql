
-- Insert roles if they do not exist for testing (optional, since RoleDataSeeder handles this)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;

INSERT INTO wallet_tier (name, daily_funding_limit, daily_transfer_limit, daily_withdraw_limit, weekly_funding_limit, weekly_transfer_limit, weekly_withdraw_limit)
VALUES ('BASIC', 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) ON CONFLICT (name) DO NOTHING;
INSERT INTO wallet_tier (name, daily_funding_limit, daily_transfer_limit, daily_withdraw_limit, weekly_funding_limit, weekly_transfer_limit, weekly_withdraw_limit)
VALUES ('SILVER', 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) ON CONFLICT (name) DO NOTHING;
INSERT INTO wallet_tier (name, daily_funding_limit, daily_transfer_limit, daily_withdraw_limit, weekly_funding_limit, weekly_transfer_limit, weekly_withdraw_limit)
VALUES ('GOLD', 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) ON CONFLICT (name) DO NOTHING;

-- Insert the admin user with additional details
INSERT INTO users (first_name, last_name, middle_name, email, password, date_of_birth, address, bvn, created_at, enabled)
VALUES (
           'Admin',
           'User',
           NULL,
           'adminsuper@example.com',
           '$2a$10$SaCAPZDet1fSFPDtMW5rwuhWzNz9PozqdzOHK/2iLrK0MuRePFNzq',
           '1990-01-01',  -- Example Date of Birth in YYYY-MM-DD format
           '123 Admin St, Admin City, AC 12345',  -- Example Address
           '12345678901',  -- Example BVN (Bank Verification Number)
           NOW(),  -- Current timestamp for created_at
           TRUE   -- Enabled status
       );

-- Insert into user_roles to associate the admin user with the role
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE email = 'adminsuper@example.com'),
           (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
       );

-- Ensure the name column is unique
ALTER TABLE roles ADD CONSTRAINT unique_role_name UNIQUE (name);
ALTER TABLE wallet_tier ADD CONSTRAINT unique_wallet_tier_name UNIQUE (name);

