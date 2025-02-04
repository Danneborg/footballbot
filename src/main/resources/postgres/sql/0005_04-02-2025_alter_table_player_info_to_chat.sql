DROP TABLE player_info_to_chat;

CREATE TABLE player_info_to_chat
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    player_id  BIGINT NOT NULL,
    tg_chat_id BIGINT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player_info (id) ON DELETE CASCADE,
    FOREIGN KEY (tg_chat_id) REFERENCES chat (tg_chat_id) ON DELETE CASCADE
);