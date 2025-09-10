-- уникальный инвентарный номер в рамках админа
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'ux_book_item_admin_inv'
    ) THEN
CREATE UNIQUE INDEX ux_book_item_admin_inv
    ON book_item(added_by_admin_id, inventory_code);
END IF;
END$$;

-- ускоряем поиск по QR
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'ux_book_item_qr_token'
    ) THEN
CREATE UNIQUE INDEX ux_book_item_qr_token ON book_item(qr_token);
END IF;
END$$;

-- тестовые пользователи (подставь имейлы при необходимости)
-- пароли закодированы BCrypt: admin123 / user123
INSERT INTO users (username, email, password_hash, role)
SELECT 'admin', 'admin@example.com',
       '$2a$10$yO7oVJmX3Wc1t3L9j8l1Auu8l3XwR7u7YI1mN7m4qgQ8p4f7l5l2i', 'ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

INSERT INTO users (username, email, password_hash, role)
SELECT 'user', 'user@example.com',
       '$2a$10$XK2Wq5yq3M/V8mX9Cz8t8eTQp8x2l4b3E0mP2t6m8m3O2H6s1xHVO', 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='user');
