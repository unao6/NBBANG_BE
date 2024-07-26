-- Users
INSERT INTO users (name) VALUES ('유저1');
INSERT INTO users (name) VALUES ('유저2');
INSERT INTO users (name) VALUES ('유저3');
INSERT INTO users (name) VALUES ('유저4');
INSERT INTO users (name) VALUES ('유저5');
INSERT INTO users (name) VALUES ('유저6');
INSERT INTO users (name) VALUES ('유저7');
INSERT INTO users (name) VALUES ('유저8');
INSERT INTO users (name) VALUES ('유저9');
INSERT INTO users (name) VALUES ('유저10');

-- Payments for 유저1
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (1, 'partnerUser1', 'order1', 'CARD', 1000, 'APPROVED', '2023-07-01 12:00:00', 'cid1', 'tid1', '2023-07-01 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (1, 'partnerUser1', 'order2', 'CARD', 2000, 'REFUND_CANCELLED', '2023-07-02 12:00:00', 'cid2', 'tid2', '2023-07-02 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (1, 'partnerUser1', 'order3', 'CARD', 3000, 'CANCELED', '2023-07-03 12:00:00', 'cid3', 'tid3', '2023-07-03 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (1, 'partnerUser1', 'order4', 'CARD', 4000, 'APPROVED', '2023-07-04 12:00:00', 'cid4', 'tid4', '2023-07-04 12:10:00');

-- Payments for 유저2
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (2, 'partnerUser2', 'order1', 'CARD', 1500, 'REFUND_REQUESTED', '2023-07-05 12:00:00', 'cid5', 'tid5', '2023-07-05 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (2, 'partnerUser2', 'order2', 'CARD', 2500, 'CREATED', '2023-07-06 12:00:00', 'cid6', 'tid6', '2023-07-06 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (2, 'partnerUser2', 'order3', 'CARD', 3500, 'CANCELED', '2023-07-07 12:00:00', 'cid7', 'tid7', '2023-07-07 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (2, 'partnerUser2', 'order4', 'CARD', 4500, 'APPROVED', '2023-07-08 12:00:00', 'cid8', 'tid8', '2023-07-08 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (2, 'partnerUser2', 'order5', 'CARD', 5500, 'REFUND_REQUESTED', '2023-07-09 12:00:00', 'cid9', 'tid9', '2023-07-09 12:10:00');

-- Payments for 유저3
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (3, 'partnerUser3', 'order1', 'CARD', 1100, 'APPROVED', '2023-07-10 12:00:00', 'cid10', 'tid10', '2023-07-10 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (3, 'partnerUser3', 'order2', 'CARD', 2100, 'REFUND_CANCELLED', '2023-07-11 12:00:00', 'cid11', 'tid11', '2023-07-11 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (3, 'partnerUser3', 'order3', 'CARD', 3100, 'CANCELED', '2023-07-12 12:00:00', 'cid12', 'tid12', '2023-07-12 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (3, 'partnerUser3', 'order4', 'CARD', 4100, 'REFUND_REQUESTED', '2023-07-13 12:00:00', 'cid13', 'tid13', '2023-07-13 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (3, 'partnerUser3', 'order5', 'CARD', 5100, 'REFUNDED_COMPLETED', '2023-07-14 12:00:00', 'cid14', 'tid14', '2023-07-14 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (3, 'partnerUser3', 'order6', 'CARD', 6100, 'REFUND_FAILED', '2023-07-15 12:00:00', 'cid15', 'tid15', '2023-07-15 12:10:00');

-- Payments for 유저4
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (4, 'partnerUser4', 'order1', 'CARD', 1200, 'REFUND_REQUESTED', '2023-07-16 12:00:00', 'cid16', 'tid16', '2023-07-16 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (4, 'partnerUser4', 'order2', 'CARD', 2200, 'CREATED', '2023-07-17 12:00:00', 'cid17', 'tid17', '2023-07-17 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (4, 'partnerUser4', 'order3', 'CARD', 3200, 'CANCELED', '2023-07-18 12:00:00', 'cid18', 'tid18', '2023-07-18 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (4, 'partnerUser4', 'order4', 'CARD', 4200, 'REFUND_REQUESTED', '2023-07-19 12:00:00', 'cid19', 'tid19', '2023-07-19 12:10:00');

-- Payments for 유저5
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (5, 'partnerUser5', 'order1', 'CARD', 1300, 'APPROVED', '2023-07-20 12:00:00', 'cid20', 'tid20', '2023-07-20 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (5, 'partnerUser5', 'order2', 'CARD', 2300, 'CREATED', '2023-07-21 12:00:00', 'cid21', 'tid21', '2023-07-21 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (5, 'partnerUser5', 'order3', 'CARD', 3300, 'CANCELED', '2023-07-22 12:00:00', 'cid22', 'tid22', '2023-07-22 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (5, 'partnerUser5', 'order4', 'CARD', 4300, 'REFUND_FAILED', '2023-07-23 12:00:00', 'cid23', 'tid23', '2023-07-23 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (5, 'partnerUser5', 'order5', 'CARD', 5300, 'REFUND_FAILED', '2023-07-24 12:00:00', 'cid24', 'tid24', '2023-07-24 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (5, 'partnerUser5', 'order6', 'CARD', 6300, 'APPROVED', '2023-07-25 12:00:00', 'cid25', 'tid25', '2023-07-25 12:10:00');

-- Payments for 유저6
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (6, 'partnerUser6', 'order1', 'CARD', 1400, 'APPROVED', '2023-07-26 12:00:00', 'cid26', 'tid26', '2023-07-26 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (6, 'partnerUser6', 'order2', 'CARD', 2400, 'CREATED', '2023-07-27 12:00:00', 'cid27', 'tid27', '2023-07-27 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (6, 'partnerUser6', 'order3', 'CARD', 3400, 'CANCELED', '2023-07-28 12:00:00', 'cid28', 'tid28', '2023-07-28 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (6, 'partnerUser6', 'order4', 'CARD', 4400, 'APPROVED', '2023-07-29 12:00:00', 'cid29', 'tid29', '2023-07-29 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (6, 'partnerUser6', 'order5', 'CARD', 5400, 'APPROVED', '2023-07-30 12:00:00', 'cid30', 'tid30', '2023-07-30 12:10:00');

-- Payments for 유저7
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order1', 'CARD', 1500, 'APPROVED', '2023-07-31 12:00:00', 'cid31', 'tid31', '2023-07-31 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order2', 'CARD', 2500, 'CREATED', '2023-08-01 12:00:00', 'cid32', 'tid32', '2023-08-01 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order3', 'CARD', 3500, 'CANCELED', '2023-08-02 12:00:00', 'cid33', 'tid33', '2023-08-02 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order4', 'CARD', 4500, 'APPROVED', '2023-08-03 12:00:00', 'cid34', 'tid34', '2023-08-03 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order5', 'CARD', 5500, 'APPROVED', '2023-08-04 12:00:00', 'cid35', 'tid35', '2023-08-04 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order6', 'CARD', 6500, 'APPROVED', '2023-08-05 12:00:00', 'cid36', 'tid36', '2023-08-05 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order7', 'CARD', 7500, 'APPROVED', '2023-08-06 12:00:00', 'cid37', 'tid37', '2023-08-06 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order8', 'CARD', 8500, 'APPROVED', '2023-08-07 12:00:00', 'cid38', 'tid38', '2023-08-07 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order9', 'CARD', 9500, 'APPROVED', '2023-08-08 12:00:00', 'cid39', 'tid39', '2023-08-08 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (7, 'partnerUser7', 'order10', 'CARD', 10500, 'APPROVED', '2023-08-09 12:00:00', 'cid40', 'tid40', '2023-08-09 12:10:00');

-- Payments for 유저8
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order1', 'CARD', 1600, 'APPROVED', '2023-08-10 12:00:00', 'cid41', 'tid41', '2023-08-10 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order2', 'CARD', 2600, 'CREATED', '2023-08-11 12:00:00', 'cid42', 'tid42', '2023-08-11 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order3', 'CARD', 3600, 'CANCELED', '2023-08-12 12:00:00', 'cid43', 'tid43', '2023-08-12 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order4', 'CARD', 4600, 'APPROVED', '2023-08-13 12:00:00', 'cid44', 'tid44', '2023-08-13 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order5', 'CARD', 5600, 'APPROVED', '2023-08-14 12:00:00', 'cid45', 'tid45', '2023-08-14 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order6', 'CARD', 6600, 'APPROVED', '2023-08-15 12:00:00', 'cid46', 'tid46', '2023-08-15 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (8, 'partnerUser8', 'order7', 'CARD', 7600, 'APPROVED', '2023-08-16 12:00:00', 'cid47', 'tid47', '2023-08-16 12:10:00');

-- Payments for 유저9
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (9, 'partnerUser9', 'order1', 'CARD', 1700, 'APPROVED', '2023-08-17 12:00:00', 'cid48', 'tid48', '2023-08-17 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (9, 'partnerUser9', 'order2', 'CARD', 2700, 'CREATED', '2023-08-18 12:00:00', 'cid49', 'tid49', '2023-08-18 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (9, 'partnerUser9', 'order3', 'CARD', 3700, 'CANCELED', '2023-08-19 12:00:00', 'cid50', 'tid50', '2023-08-19 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (9, 'partnerUser9', 'order4', 'CARD', 4700, 'APPROVED', '2023-08-20 12:00:00', 'cid51', 'tid51', '2023-08-20 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (9, 'partnerUser9', 'order5', 'CARD', 5700, 'APPROVED', '2023-08-21 12:00:00', 'cid52', 'tid52', '2023-08-21 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (9, 'partnerUser9', 'order6', 'CARD', 6700, 'APPROVED', '2023-08-22 12:00:00', 'cid53', 'tid53', '2023-08-22 12:10:00');

-- Payments for 유저10
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (10, 'partnerUser10', 'order1', 'CARD', 1800, 'APPROVED', '2023-08-23 12:00:00', 'cid54', 'tid54', '2023-08-23 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (10, 'partnerUser10', 'order2', 'CARD', 2800, 'CREATED', '2023-08-24 12:00:00', 'cid55', 'tid55', '2023-08-24 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (10, 'partnerUser10', 'order3', 'CARD', 3800, 'CANCELED', '2023-08-25 12:00:00', 'cid56', 'tid56', '2023-08-25 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (10, 'partnerUser10', 'order4', 'CARD', 4800, 'APPROVED', '2023-08-26 12:00:00', 'cid57', 'tid57', '2023-08-26 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (10, 'partnerUser10', 'order5', 'CARD', 5800, 'APPROVED', '2023-08-27 12:00:00', 'cid58', 'tid58', '2023-08-27 12:10:00');
INSERT INTO payment (user_id, partner_user_id, partner_order_id, payment_type, amount, status, payment_created_at, cid, tid, payment_approved_at)
VALUES (10, 'partnerUser10', 'order6', 'CARD', 6800, 'APPROVED', '2023-08-28 12:00:00', 'cid59', 'tid59', '2023-08-28 12:10:00');
