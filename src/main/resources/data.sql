-- Create sequence for person IDs starting after admin
CREATE SEQUENCE IF NOT EXISTS person_seq START WITH 2;

-- Insert default admin user
INSERT INTO persons (id, name, email, password, enabled, role, created_at)
VALUES (
    1,
    'Admin One',
    'admin1@gmail.com',
    '$2a$10$RlS1CzPzwdl2ClB8sjmMuO.1.46P0d4JoDE14kYgmzrXIIUGWUKPC',
    true,
    'ADMIN',
    CURRENT_TIMESTAMP
);

-- Insert admin wallet
INSERT INTO wallet (id, user_id, balance, status, verification_level, created_at, updated_at)
VALUES (
    1,
    1,
    1000000.00, -- Starting balance of 1M
    'ACTIVE',
    'VERIFIED',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
); 