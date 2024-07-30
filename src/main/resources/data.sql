-- -- OTT
-- INSERT INTO ott (name, price, capacity, created, modified)
-- VALUES ('Netflix', 15000, 4, NOW(), NOW());
--
-- INSERT INTO ott (name, price, capacity, created, modified)
-- VALUES ('Disney+', 9900, 4, NOW(), NOW());
--
-- INSERT INTO ott (name, price, capacity, created, modified)
-- VALUES ('Amazon Prime', 12000, 6, NOW(), NOW());
--
-- INSERT INTO ott (name, price, capacity, created, modified)
-- VALUES ('Hulu', 8000, 5, NOW(), NOW());
--
-- INSERT INTO ott (name, price, capacity, created, modified)
-- VALUES ('Apple TV+', 6500, 6, NOW(), NOW());

INSERT INTO user (nickname, email, password, phone_number, role, created_at, modified_at)
VALUES ('user', 'user@a.com', '$2a$10$uHYGqB7MajXMLnDSkchgh.c1AU7x.SVlp99e3C/2iDUtgtv1/P.AG', '010-1111-1111', 'USER', NOW(), NOW());

INSERT INTO user (nickname, email, password, phone_number, role, created_at, modified_at)
VALUES ('admin', 'admin@a.com', '$2a$10$uHYGqB7MajXMLnDSkchgh.c1AU7x.SVlp99e3C', '010-2222-2222', 'ADMIN', NOW(), NOW());

INSERT INTO user (nickname, email, password, phone_number, role, created_at, modified_at)
VALUES ('user3', 'user3@example.com', 'password3', '010-3333-3333', 'USER', NOW(), NOW());


INSERT INTO ott (name, price, capacity) VALUES
('GPT', 5000, 5),
('Netflix', 5000, 5),
('Disney+', 5000, 5);
