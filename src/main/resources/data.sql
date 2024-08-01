-- OTT
INSERT INTO ott (name, price, capacity, created_at, modified_at)
VALUES ('디즈니플러스', 9900, 4, NOW(), NOW());

INSERT INTO ott (name, price, capacity, created_at, modified_at)
VALUES ('ChatGPT', 29900, 4, NOW(), NOW());

INSERT INTO ott (name, price, capacity, created_at, modified_at)
VALUES ('티빙', 9900, 4, NOW(), NOW());

# INSERT INTO ott (name, price, capacity, created_at, modified_at)
# VALUES ('Amazon Prime', 12000, 6, NOW(), NOW());

# INSERT INTO ott (name, price, capacity, created_at, modified_at)
# VALUES ('Hulu', 8000, 5, NOW(), NOW());
#
# INSERT INTO ott (name, price, capacity, created_at, modified_at)
# VALUES ('Apple TV+', 6500, 6, NOW(), NOW());

INSERT INTO user (nickname, email, password, phone_number, role, created_at, modified_at)
VALUES ('user1', 'user1@example.com', 'password1', '010-1111-1111', 'ROLE_USER', NOW(), NOW());

INSERT INTO user (nickname, email, password, phone_number, role, created_at, modified_at)
VALUES ('user2', 'user2@example.com', 'password2', '010-2222-2222', 'ROLE_ADMIN', NOW(), NOW());

INSERT INTO user (nickname, email, password, phone_number, role, created_at, modified_at)
VALUES ('user3', 'user3@example.com', 'password3', '010-3333-3333', 'ROLE_USER', NOW(), NOW());