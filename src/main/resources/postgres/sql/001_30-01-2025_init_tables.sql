CREATE TABLE game_session
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_id     BIGINT    NOT NULL,
    start_date       TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    is_finished BOOLEAN
);

CREATE TABLE player_info
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tg_name         VARCHAR(100) NOT NULL,
    tg_visible_name VARCHAR(100)
);

CREATE TABLE roster
(
    id                        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    game_session_id           BIGINT      NOT NULL,
    team_colour               VARCHAR(50) NOT NULL,
    played_in_not_full_roster BOOLEAN,
    FOREIGN KEY (game_session_id) REFERENCES game_session (id) ON DELETE CASCADE
);

CREATE TABLE single_game_result
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    game_session_id  BIGINT  NOT NULL,
    winner_roster_id BIGINT  NOT NULL,
    looser_roster_id BIGINT  NOT NULL,
    winner_score     BIGINT  NOT NULL,
    looser_score     BIGINT  NOT NULL,
    is_draw          BOOLEAN NOT NULL,
    FOREIGN KEY (game_session_id) REFERENCES game_session (id) ON DELETE CASCADE,
    FOREIGN KEY (winner_roster_id) REFERENCES roster (id) ON DELETE CASCADE,
    FOREIGN KEY (looser_roster_id) REFERENCES roster (id) ON DELETE CASCADE
);

CREATE TABLE single_goal_info
(
    id                    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    game_session_id       BIGINT NOT NULL,
    single_game_result_id BIGINT NOT NULL,
    roster_id             BIGINT NOT NULL,
    bombardier_id         BIGINT NOT NULL,
    assistant_id          BIGINT,
    FOREIGN KEY (game_session_id) REFERENCES game_session (id) ON DELETE CASCADE,
    FOREIGN KEY (single_game_result_id) REFERENCES single_game_result (id) ON DELETE CASCADE,
    FOREIGN KEY (roster_id) REFERENCES roster (id) ON DELETE CASCADE,
    FOREIGN KEY (bombardier_id) REFERENCES player_info (id) ON DELETE CASCADE,
    FOREIGN KEY (assistant_id) REFERENCES player_info (id) ON DELETE SET NULL
);

CREATE TABLE player_info_to_roster
(
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    player_id BIGINT NOT NULL,
    roster_id BIGINT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player_info (id) ON DELETE CASCADE,
    FOREIGN KEY (roster_id) REFERENCES roster (id) ON DELETE CASCADE
);