/*
Navicat MySQL Data Transfer

Source Server         : MN Local
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : nariz-iot

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2016-12-27 15:17:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `v_mac` varchar(255) NOT NULL,
  `v_id_home` varchar(255) NOT NULL,
  `v_name` varchar(255) DEFAULT NULL,
  `v_hostname` varchar(255) DEFAULT NULL,
  `v_ip` varchar(20) DEFAULT NULL,
  `v_connect` varchar(10) DEFAULT 'false',
  `d_last_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`v_mac`),
  KEY `v_id_home_fk` (`v_id_home`) USING BTREE,
  KEY `v_mac_idx` (`v_mac`) USING BTREE,
  CONSTRAINT `device_ibfk_1` FOREIGN KEY (`v_id_home`) REFERENCES `home` (`v_id_home`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES ('e4:90:7e:6e:09:80', 'HOSHI', 'MotoE2', 'motoe2', '', 'false', '2016-12-27 15:07:31');

-- ----------------------------
-- Table structure for home
-- ----------------------------
DROP TABLE IF EXISTS `home`;
CREATE TABLE `home` (
  `v_id_home` varchar(255) NOT NULL,
  `v_name` varchar(255) DEFAULT NULL,
  `v_address` varchar(255) DEFAULT NULL,
  KEY `v_id_home_idx` (`v_id_home`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of home
-- ----------------------------
INSERT INTO `home` VALUES ('HOSHI', 'ARMANDOSHOUSE', 'Av. Manuel Avila Camacho #51');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `v_id_user` varchar(255) NOT NULL,
  `v_id_home` varchar(255) NOT NULL,
  `v_name` varchar(300) DEFAULT NULL,
  `v_mail` varchar(200) DEFAULT NULL,
  `v_number` varchar(50) DEFAULT NULL,
  `v_passwd` varchar(50) NOT NULL,
  KEY `v_id_home_fx` (`v_id_home`) USING BTREE,
  KEY `v_id_user_idx` (`v_id_user`) USING BTREE,
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`v_id_home`) REFERENCES `home` (`v_id_home`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('ARMANDOW', 'HOSHI', 'Armando Castillo', 'armandowg@gmail.com', '5517854235', 'yx99+U5qd671FtTfOJUgIQ==');

-- ----------------------------
-- Table structure for userandroid
-- ----------------------------
DROP TABLE IF EXISTS `userandroid`;
CREATE TABLE `userandroid` (
  `v_id_user` varchar(255) NOT NULL,
  `v_id_android` varchar(400) NOT NULL,
  KEY `v_id_user_fk` (`v_id_user`) USING BTREE,
  CONSTRAINT `userandroid_ibfk_1` FOREIGN KEY (`v_id_user`) REFERENCES `user` (`v_id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userandroid
-- ----------------------------
INSERT INTO `userandroid` VALUES ('ARMANDOW', 'cOSISXu4JTk:APA91bHicrK2SlzSy5qXhcDiKirI_mLgJ9YLqH6ZN9uI2xMhdFN-BvXhpTU0Mn8CmZNfnRrRVlSKuhknMLYJs4p_KjNePFqZk6C2-Tex5iBoPRffdNkRFF7DKVCQ-JkqOEGYvr8kIiqB');

-- ----------------------------
-- Procedure structure for SP_Edit_Device
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_Edit_Device`;
DELIMITER ;;
CREATE DEFINER=`productosmn`@`localhost` PROCEDURE `SP_Edit_Device`(p_mac VARCHAR(255), p_id_home VARCHAR(255), p_name VARCHAR(255), p_ip VARCHAR(20), p_hostname VARCHAR(20),  p_connect VARCHAR(10), OUT p_exito CHAR(1), OUT p_mensaje VARCHAR(100))
BEGIN
	DECLARE home VARCHAR(255);
	DECLARE mac VARCHAR(255);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		SET p_exito = 'N';
		SET p_mensaje = 'Error en SP_New_Device';
	END;

	START TRANSACTION;
	SELECT v_id_home INTO home FROM home WHERE v_id_home = p_id_home;

	IF home IS NULL THEN
		SET p_exito = 'N';
		SET p_mensaje = CONCAT('NO EXISTE LA CASA: ''', p_id_home, '''.');
	ELSE
		UPDATE device
		SET v_name = p_name, v_ip = p_ip, v_connect = p_connect, v_hostname = p_hostname, d_last_update = NOW()
		WHERE v_mac = p_mac AND v_id_home = p_id_home;
		
		SET p_exito = 'N';
		SET p_mensaje = CONCAT('SE MODIFICO: ''', p_name, '''.');
		COMMIT;
	END IF;   
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_Inserta_Android
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_Inserta_Android`;
DELIMITER ;;
CREATE DEFINER=`productosmn`@`localhost` PROCEDURE `SP_Inserta_Android`(IN p_id_usuario VARCHAR(50), IN p_device VARCHAR(300),  OUT p_exito CHAR(1), OUT p_mensaje VARCHAR(100))
BEGIN
	DECLARE usr VARCHAR(50);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		SET p_exito = 'N';
		SET p_mensaje = 'Error en SP_Inserta_Android.';
	END;

	START TRANSACTION;
	SELECT v_id_user INTO usr FROM userandroid WHERE v_id_user = p_id_usuario;

	IF usr IS NULL THEN
		SET p_exito = 'N';
		SET p_mensaje = CONCAT('El usuario: ', p_id_usuario, ' no existe');
	ELSE
		SELECT v_id_user INTO usr FROM userandroid WHERE v_id_user = p_id_usuario AND v_id_android = p_device;

		IF usr IS NOT NULL THEN
			INSERT INTO userandroid (v_id_user, v_id_android) VALUES (p_id_usuario, p_device);
			SET p_exito = 'S';
			SET p_mensaje = CONCAT('Se inserto android para: ', p_id_usuario);
		ELSE
			SET p_exito = 'N';
			SET p_mensaje = CONCAT('El usuario: ', p_id_usuario, ' ya tiene el android asociado');
		END IF;
	END IF;

	COMMIT;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_New_Android
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_New_Android`;
DELIMITER ;;
CREATE DEFINER=`hoshi`@`%` PROCEDURE `SP_New_Android`(p_id_user VARCHAR(255), p_id_android VARCHAR(255), OUT p_exito CHAR(1), OUT p_mensaje VARCHAR(100))
BEGIN
	DECLARE usr VARCHAR(255);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		SET p_exito = 'N';
		SET p_mensaje = 'Error en SP_New_Device';
	END;

	START TRANSACTION;
	SELECT v_id_user INTO usr FROM userandroid WHERE v_id_user = p_id_user AND v_id_android = p_id_android;

	IF usr IS NULL THEN
		INSERT INTO userandroid (v_id_user, v_id_android) VALUES (p_id_user, p_id_android);
		COMMIT;
		SET p_exito = 'S';
		SET p_mensaje = CONCAT('ID ASOCIADO A: ''', p_id_user, '''.');
	ELSE
		SET p_exito = 'N';
		SET p_mensaje = CONCAT('EL ID YA ESTA ASOCIADO CON: ''', p_id_user, '''.');
	END IF;   
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_New_Device
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_New_Device`;
DELIMITER ;;
CREATE DEFINER=`productosmn`@`localhost` PROCEDURE `SP_New_Device`(IN p_mac VARCHAR(255), IN p_id_home VARCHAR(255), IN p_name VARCHAR(255), IN p_ip VARCHAR(20), IN p_hostname VARCHAR(20), OUT p_exito CHAR(1), OUT p_mensaje VARCHAR(100))
BEGIN
	DECLARE home VARCHAR(255);
	DECLARE mac VARCHAR(255);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		SET p_exito = 'N';
		SET p_mensaje = 'Error en SP_New_Device';
	END;

	START TRANSACTION;
	SELECT v_id_home INTO home FROM home WHERE v_id_home = p_id_home;

	IF home IS NULL THEN
		SET p_exito = 'N';
		SET p_mensaje = CONCAT('NO EXISTE LA CASA: ''', p_id_home, '''.');
	ELSE
		SELECT v_mac INTO mac FROM device WHERE v_mac = p_mac;

		IF mac IS NULL THEN
			INSERT INTO device(v_mac,v_id_home,v_name,v_ip,v_hostname,d_last_update) VALUES (p_mac,p_id_home,p_name,p_ip,p_hostname,NOW());
			SET p_exito = 'S';
			SET p_mensaje = 'DISPOSITIVO AGREGADO';
		ELSE
			SET p_exito = 'N';
			SET p_mensaje = CONCAT('EL DISPOSITIVO YA EXISTE EN: ''', p_id_home, '''.');
		END IF;
	END IF;   
	COMMIT;
END
;;
DELIMITER ;
