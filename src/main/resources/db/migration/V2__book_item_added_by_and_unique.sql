-- кто добавил книгу
ALTER TABLE book_item
    ADD COLUMN added_by_admin_id BIGINT NOT NULL REFERENCES users(id);

-- уникальность инвентарного номера в рамках админа
ALTER TABLE book_item
    ADD CONSTRAINT uq_admin_inventory UNIQUE (added_by_admin_id, inventory_code);

-- индексы для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_book_item_admin ON book_item(added_by_admin_id);
CREATE INDEX IF NOT EXISTS idx_book_item_qr ON book_item(qr_token);
