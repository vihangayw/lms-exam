-- FUNCTIONS -------------------------------
SET GLOBAL log_bin_trust_function_creators = 1;

INSERT INTO configuration (id, val)
VALUES ('local_path', '/opt/tomcat/webapps/ROOT/lms-mc/');
INSERT INTO configuration (id, val)
VALUES ('server_base_url', 'https://dev.ts.lk/lms-mc/');

ALTER TABLE exam_pic
    AUTO_INCREMENT = 8000;

ALTER TABLE exam_preflight
    AUTO_INCREMENT = 1000;


