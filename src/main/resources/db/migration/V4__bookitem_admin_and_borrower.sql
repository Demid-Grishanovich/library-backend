-- V4__bookitem_admin_and_borrower.sql

-- 1) Добавляем колонки (здесь IF NOT EXISTS допустим)
ALTER TABLE book_item ADD COLUMN IF NOT EXISTS added_by_admin_id BIGINT;
ALTER TABLE book_item ADD COLUMN IF NOT EXISTS borrower_id      BIGINT;

-- 2) Бэкапаем данные для NOT NULL (подставь реальный id админа вместо 1)
UPDATE book_item
SET added_by_admin_id = 1
WHERE added_by_admin_id IS NULL;

-- 3) Делаем колонку NOT NULL (без IF NOT EXISTS – через DO-блок с проверкой)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'book_item'
      AND column_name = 'added_by_admin_id'
      AND is_nullable = 'YES'
  ) THEN
    EXECUTE 'ALTER TABLE book_item ALTER COLUMN added_by_admin_id SET NOT NULL';
END IF;
END $$;

-- 4) Внешние ключи — добавляем только если их ещё нет
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_book_item_admin') THEN
ALTER TABLE book_item
    ADD CONSTRAINT fk_book_item_admin
        FOREIGN KEY (added_by_admin_id) REFERENCES users(id);
END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_book_item_borrower') THEN
ALTER TABLE book_item
    ADD CONSTRAINT fk_book_item_borrower
        FOREIGN KEY (borrower_id) REFERENCES users(id);
END IF;
END $$;
