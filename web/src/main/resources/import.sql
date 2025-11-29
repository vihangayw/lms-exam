-- FUNCTIONS -------------------------------
SET GLOBAL log_bin_trust_function_creators = 1;

INSERT INTO configuration (id, val)
VALUES ('local_path', '/opt/tomcat/webapps/ROOT/lms-mc/');
INSERT INTO configuration (id, val)
VALUES ('hibernate_seq', '1760585812000');
INSERT INTO configuration (id, val)
VALUES ('server_base_url', 'http://103.1.179.245:8080/lms-mc/');

ALTER TABLE exam_pic
    AUTO_INCREMENT = 8000;

ALTER TABLE exam_preflight
    AUTO_INCREMENT = 1000;


