ALTER TABLE chat
    ADD CONSTRAINT tg_chat_id_uniq UNIQUE (tg_chat_id);


CREATE TABLE chat_step
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tg_chat_id BIGINT       NOT NULL,
    command    VARCHAR(255) NOT NULL,
    step_time  TIMESTAMP    NOT NULL,
    is_last    BOOLEAN      NOT NULL,
    FOREIGN KEY (tg_chat_id) REFERENCES chat (tg_chat_id) ON DELETE CASCADE
);

-- Создание индексов
CREATE INDEX idx_tg_chat_id ON chat_step (tg_chat_id);
CREATE INDEX idx_tg_chat_id_is_last ON chat_step (tg_chat_id, is_last);