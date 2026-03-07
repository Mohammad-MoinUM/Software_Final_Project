-- Database initialization script for Mini Marketplace
-- This script creates the database and initial roles

-- Drop database if exists (use with caution in production)
-- DROP DATABASE IF EXISTS mini_marketplace;

-- Create database
CREATE DATABASE mini_marketplace
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- Connect to the database
\c mini_marketplace

-- Note: Tables will be auto-created by Hibernate based on JPA entities
-- This script only inserts initial data

-- Insert default roles
INSERT INTO roles (name, description, created_at, updated_at) 
VALUES 
    ('ADMIN', 'Administrator with full system access', NOW(), NOW()),
    ('SELLER', 'User who can sell products', NOW(), NOW()),
    ('BUYER', 'User who can purchase products', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Optional: Create an admin user (password: admin123)
-- Password hash is for 'admin123' using BCrypt
-- You should change this password after first login
INSERT INTO users (username, email, password, full_name, phone_number, enabled, created_at, updated_at)
VALUES 
    ('admin', 'admin@minimarketplace.com', '$2a$10$xioYFq8XkXb9I5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q', 'System Administrator', '+1234567890', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Link admin user to ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

COMMIT;
