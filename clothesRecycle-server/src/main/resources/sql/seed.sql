INSERT INTO campus (name, address, status, remark)
VALUES ('主校区', '示例大学主校区', 1, '系统初始化校区')
ON DUPLICATE KEY UPDATE update_time = NOW();

INSERT INTO admin (phone, password, name, role, campus_id, status)
VALUES ('18800000000', '123456', '系统管理员', 'SUPER_ADMIN', NULL, 1)
ON DUPLICATE KEY UPDATE update_time = NOW();

INSERT INTO drop_point (campus_id, name, location_desc, open_time, manager_note, stock_count, status)
VALUES (1, '主校区东门回收点', '东门快递站旁', '09:00-18:00', '工作日值守', 0, 1);

INSERT INTO gift (name, description, image_url, point_cost, stock, exchanged_count, status)
VALUES ('环保帆布袋', '校园联名环保帆布袋', '', 120, 100, 0, 1),
       ('不锈钢保温杯', '保温杯 480ml', '', 260, 80, 0, 1);
