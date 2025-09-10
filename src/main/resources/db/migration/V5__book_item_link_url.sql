-- Добавляем статичную ссылку на фронт для каждой книги
ALTER TABLE book_item
    ADD COLUMN IF NOT EXISTS link_url VARCHAR(512) NOT NULL DEFAULT '';

-- Для уже существующих — заполним дефолтом (можешь поправить базовый домен)
UPDATE book_item
SET link_url = CONCAT('http://localhost:5173/books/', id)
WHERE link_url = '' OR link_url IS NULL;
