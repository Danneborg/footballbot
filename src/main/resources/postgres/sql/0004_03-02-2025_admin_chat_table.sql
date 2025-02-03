CREATE TABLE admin_in_chat
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tg_chat_user_id  BIGINT    NOT NULL,
    tg_group_chat_id BIGINT    NOT NULL,
    assign_time        TIMESTAMP NOT NULL,

    FOREIGN KEY (tg_chat_user_id) REFERENCES chat (tg_chat_id) ON DELETE CASCADE,
    FOREIGN KEY (tg_group_chat_id) REFERENCES chat (tg_chat_id) ON DELETE CASCADE
);

-- Создание индексов
CREATE INDEX idx_tg_chat_user_id ON admin_in_chat (tg_chat_user_id);
CREATE INDEX idx_tg_group_chat_id ON admin_in_chat (tg_group_chat_id);