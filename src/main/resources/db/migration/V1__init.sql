-- USERS
CREATE TABLE users (
                       id             BIGSERIAL PRIMARY KEY,
                       username       VARCHAR(50)  NOT NULL UNIQUE,
                       password_hash  VARCHAR(100) NOT NULL,
                       email          VARCHAR(255) NOT NULL UNIQUE,
                       role           VARCHAR(20)  NOT NULL,            -- ADMIN | USER
                       created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                       updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- BOOK_ITEM
CREATE TABLE book_item (
                           id             BIGSERIAL PRIMARY KEY,
                           title          VARCHAR(255) NOT NULL,
                           author         VARCHAR(255) NOT NULL,
                           year           INT,
                           inventory_code VARCHAR(100) NOT NULL UNIQUE,
                           qr_token       VARCHAR(100) NOT NULL UNIQUE,
                           status         VARCHAR(20)  NOT NULL,            -- AVAILABLE|BORROWED|PENDING_RETURN|LOST
                           created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                           updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- LOAN
CREATE TABLE loan (
                      id                         BIGSERIAL PRIMARY KEY,
                      user_id                    BIGINT NOT NULL REFERENCES users(id),
                      book_item_id               BIGINT NOT NULL REFERENCES book_item(id),
                      borrowed_at                TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                      due_at                     TIMESTAMPTZ,
                      user_marked_return_at      TIMESTAMPTZ,
                      admin_confirmed_return_at  TIMESTAMPTZ,
                      status                     VARCHAR(20) NOT NULL   -- ACTIVE|PENDING_RETURN|RETURNED|OVERDUE
);

-- Индексы
CREATE INDEX idx_loan_user ON loan(user_id);
CREATE INDEX idx_loan_book ON loan(book_item_id);
CREATE INDEX idx_book_status ON book_item(status);

-- Check-ограничения на статусы
ALTER TABLE book_item ADD CONSTRAINT chk_book_status
    CHECK (status IN ('AVAILABLE','BORROWED','PENDING_RETURN','LOST'));
ALTER TABLE loan ADD CONSTRAINT chk_loan_status
    CHECK (status IN ('ACTIVE','PENDING_RETURN','RETURNED','OVERDUE'));
