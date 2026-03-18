-- V3__add_profanity_table.sql
CREATE TABLE IF NOT EXISTS profanity_term (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    term VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO profanity_term (term) VALUES 
('씨발'), ('시발'), ('병신'), ('개새끼'), ('지랄'), 
('ㅅㅂ'), ('ㅈㄹ'), ('ㅈ1ㄹ'), ('ㅅ1ㅂ'), ('ㅂㅅㄴ'), 
('ㅂㅅ'), ('좆'), ('이지랄'), ('샤갈'), ('꽤앵'), 
('fuck'), ('shit'), ('bitch'), ('asshole');
