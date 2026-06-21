-- FUNCTIONS -------------------------------
SET GLOBAL log_bin_trust_function_creators = 1;
SET GLOBAL max_connections = 800;


INSERT INTO configuration (id, val)
VALUES ('local_path', '/opt/tomcat/webapps/ROOT/lms-mc/');
INSERT INTO configuration (id, val)
VALUES ('lms_server_base_url', 'https://sms.metropolitancollegeedu.com/lms-mc');
INSERT INTO configuration (id, val)
VALUES ('server_base_url', 'https://exams.metropolitancollege.lk/lms-exam/');

ALTER TABLE exam_pic
    AUTO_INCREMENT = 8000;

ALTER TABLE exam_preflight
    AUTO_INCREMENT = 1000;


