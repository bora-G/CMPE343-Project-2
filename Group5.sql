-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: group5
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacts` (
  `contact_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `phone_primary` varchar(20) NOT NULL,
  `phone_secondary` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `linkedin_url` varchar(200) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`contact_id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
INSERT INTO `contacts` VALUES (1,'Ahmet',NULL,'Çelik','ahmo','5551100001',NULL,'ahmet.celik@gmail.com','https://www.linkedin.com/in/ahmetcelik','1995-03-15','2025-11-29 16:57:52','2025-11-29 16:57:52'),(2,'Ayşe',NULL,'Şahin','ayse','5551100002',NULL,'ayse.sahin@yahoo.com','https://www.linkedin.com/in/aysesahin','1996-07-21','2025-11-29 16:57:52','2025-11-29 16:57:52'),(3,'Mehmet',NULL,'Öztürk','memo','5551100003','5282440116','mehmet.ozturk@outlook.com','https://www.linkedin.com/in/mehmetozturk','1994-11-03','2025-11-29 16:57:52','2025-11-29 17:12:25'),(4,'Elif',NULL,'İnan','eli','5551100004',NULL,'elif.inan@gmail.com',NULL,'1998-01-09','2025-11-29 16:57:52','2025-11-29 16:57:52'),(5,'Can',NULL,'Ünlü','cuny','5551100005',NULL,'can.unlu@hotmail.com','https://www.linkedin.com/in/canunlu','1993-05-30','2025-11-29 16:57:52','2025-11-29 16:57:52'),(6,'Zeynep',NULL,'Kılıç','zey','5551100006','5784156522','zeynep.kilic@yandex.com','https://www.linkedin.com/in/zeynepkilic','1997-09-12','2025-11-29 16:57:52','2025-11-29 17:12:25'),(7,'Mert',NULL,'Işık','mert','5551100007',NULL,'mert.isik@gmail.com','https://www.linkedin.com/in/mertisik','1992-02-18','2025-11-29 16:57:52','2025-11-29 16:57:52'),(8,'Fatma',NULL,'Sağlam','fato','5551100008',NULL,'fatma.saglam@yahoo.com',NULL,'1999-10-25','2025-11-29 16:57:52','2025-11-29 16:57:52'),(9,'Ali',NULL,'Yüce','aliy','5551100009','5628147816','ali.yuce@gmail.com','https://www.linkedin.com/in/aliyuce','1991-06-07','2025-11-29 16:57:52','2025-11-29 17:12:25'),(10,'Derya',NULL,'Koç','dero','5551100010',NULL,'derya.koc@hotmail.com','https://www.linkedin.com/in/deryakoc','1995-12-19','2025-11-29 16:57:52','2025-11-29 16:57:52'),(11,'Ozan',NULL,'Görmüş','ozzy','5551100011',NULL,'ozan.gormus@gmail.com','https://www.linkedin.com/in/ozangormus','1996-04-11','2025-11-29 16:57:52','2025-11-29 16:57:52'),(12,'Cansu',NULL,'Özsoy','cns','5551100012','5896254478','cansu.ozsoy@outlook.com',NULL,'1994-08-23','2025-11-29 16:57:52','2025-11-29 17:12:25'),(13,'Burak',NULL,'Ağar','brk','5551100013',NULL,'burak.agar@yahoo.com','https://www.linkedin.com/in/burakagar','1993-03-02','2025-11-29 16:57:52','2025-11-29 16:57:52'),(14,'Ece',NULL,'Bölük','ece','5551100014',NULL,'ece.boluk@gmail.com','https://www.linkedin.com/in/eceboluk','1997-07-14','2025-11-29 16:57:52','2025-11-29 16:57:52'),(15,'Emre',NULL,'Köksal','emre','5551100015','5366792416','emre.koksal@gmail.com','https://www.linkedin.com/in/emrekoksal','1992-11-27','2025-11-29 16:57:52','2025-11-29 17:12:25'),(16,'Bora',NULL,'Görgün','CapJacke','5372440233',NULL,'boragorgun0@gmail.com','www.linkedin.com/in/bora-görgün','2005-01-31','2025-11-29 16:57:52','2025-12-02 18:39:05'),(17,'Kerem',NULL,'Çınar','krm','5551100017',NULL,'kerem.cinar@outlook.com','https://www.linkedin.com/in/keremcinar','1991-09-09','2025-11-29 16:57:52','2025-11-29 16:57:52'),(18,'Melis',NULL,'Gül','meli','5551100018','5372415872','melis.gul@gmail.com',NULL,'1995-02-16','2025-11-29 16:57:52','2025-11-29 17:12:25'),(19,'Onur',NULL,'Kırmızı','onursez','5551100019',NULL,'onur.kirmizi@yahoo.com','https://www.linkedin.com/in/onurkirmizi','1996-06-28','2025-11-29 16:57:52','2025-11-29 16:57:52'),(20,'İrem',NULL,'Yıldırım','irm','5551100020',NULL,'irem.yildirim@outlook.com','https://www.linkedin.com/in/iremyildirim','1994-10-04','2025-11-29 16:57:52','2025-11-29 16:57:52'),(21,'Serkan',NULL,'Şen','srk','5551100021','5694882136','serkan.sen@gmail.com','https://www.linkedin.com/in/serkansen','1993-01-20','2025-11-29 16:57:52','2025-11-29 17:12:25'),(22,'Buse',NULL,'Küçük','buse','5551100022',NULL,'buse.kucuk@yandex.com','https://www.linkedin.com/in/busekucuk','1997-05-08','2025-11-29 16:57:52','2025-11-29 16:57:52'),(23,'Hakan',NULL,'Gök','hkn','5551100023',NULL,'hakan.gok@gmail.com','https://www.linkedin.com/in/hakangok','1992-09-26','2025-11-29 16:57:52','2025-11-29 16:57:52'),(24,'Yaren',NULL,'Çifçili','yarencifcili','5551100024','5648421648','yarencifcili@stu.khas.edu.tr ','https://www.linkedin.com/in/yaren-çifçili-053687279/','2004-11-11','2025-11-29 16:57:52','2025-11-29 17:12:25'),(25,'Gökhan',NULL,'Yağcı','gkhn','5551100025',NULL,'gokhan.yagci@hotmail.com','https://www.linkedin.com/in/gokhanyagci','1991-04-06','2025-11-29 16:57:52','2025-11-29 16:57:52'),(26,'Yasemin',NULL,'Doğru','yaso','5551100026',NULL,'yasemin.dogru@gmail.com','https://www.linkedin.com/in/yasemindogru','1995-08-18','2025-11-29 16:57:52','2025-11-29 16:57:52'),(27,'Kaan','Messi','Çobanspor','messiasigi','5436546711','5284591265','cobanspor@gmail.com','https://www.linkedin.com/in/cobanspor','2005-05-07','2025-11-29 16:57:52','2025-11-29 17:12:25'),(28,'Sıla',NULL,'Güler','sila','5551100028',NULL,'sila.guler@yandex.com',NULL,'1994-06-13','2025-11-29 16:57:52','2025-11-29 16:57:52'),(29,'Deniz',NULL,'Sağer','deniz','5551100029',NULL,'deniz.sager@hotmail.com','https://www.linkedin.com/in/denizsager','1993-10-29','2025-11-29 16:57:52','2025-11-29 16:57:52'),(30,'Osman','Taha','Şahin','tahaa25452','5358455080','5893692587','taha.sahin@gmail.com','https://www.linkedin.com/in/tahasahin','2005-04-08','2025-11-29 16:57:52','2025-11-29 17:11:59'),(31,'Barış',NULL,'Gür','baris','5551100031',NULL,'baris.gur@outlook.com','https://www.linkedin.com/in/barisgur','1995-07-19','2025-11-29 16:57:52','2025-11-29 16:57:52'),(32,'Mine',NULL,'Çakır','mine','5551100032',NULL,'mine.cakir@gmail.com','https://www.linkedin.com/in/minecakir','1992-11-01','2025-11-29 16:57:52','2025-11-29 16:57:52'),(33,'Volkan',NULL,'Karagöz','volk','5551100033','5492134625','volkan.karagoz@yahoo.com',NULL,'1996-01-25','2025-11-29 16:57:52','2025-11-29 17:12:25'),(34,'İlayda',NULL,'Korkut','ily','5551100034',NULL,'ilayda.korkut@hotmail.com','https://www.linkedin.com/in/ilaydakorkut','1993-05-11','2025-11-29 16:57:52','2025-11-29 16:57:52'),(35,'Fahri','Kerem','Ön','golgexD','5551100035',NULL,'kereeemon@gmail.com','https://www.linkedin.com/in/onkerem','2005-11-03','2025-11-29 16:57:52','2025-11-29 17:12:49'),(36,'Damla',NULL,'Üstün','damla','5551100036','5491542616','damla.ustun@yandex.com','https://www.linkedin.com/in/damlaustun','1994-02-03','2025-11-29 16:57:52','2025-11-29 17:12:25'),(37,'Eren',NULL,'Ekici','Assassin','5528429211',NULL,'ekici1eren@gmail.com','https://www.linkedin.com/in/erenekici','2005-08-03','2025-11-29 16:57:52','2025-11-29 16:57:52'),(38,'Şeyma',NULL,'Gültekin','seym','5551108038',NULL,'seyma.gultekin@gmail.com',NULL,'1997-10-27','2025-11-29 16:57:52','2025-11-29 17:11:59'),(39,'Ufuk',NULL,'Eriş','ufuk','5551100039','5784681245','ufuk.eris@outlook.com','https://www.linkedin.com/in/ufukeris','1992-01-09','2025-11-29 16:57:52','2025-11-29 17:12:25'),(40,'Hilal',NULL,'Göçer','hilal','5551100040',NULL,'hilal.gocer@hotmail.com','https://www.linkedin.com/in/hilalgocer','1996-04-21','2025-11-29 16:57:52','2025-11-29 16:57:52'),(41,'Tuna',NULL,'İlgün','tuna','5551100041',NULL,'tuna.ilgun@yahoo.com','https://www.linkedin.com/in/tunailgun','1993-08-02','2025-11-29 16:57:52','2025-11-29 16:57:52'),(42,'Naz',NULL,'Şimşek','naz','5478952312','5978651235','naz.simsek@gmail.com','https://www.linkedin.com/in/nazsimsek','1995-12-14','2025-11-29 16:57:52','2025-11-29 17:12:25'),(43,'Arda',NULL,'Özbey','arda','5551100043',NULL,'arda.ozbey@outlook.com','https://www.linkedin.com/in/ardaozbey','1994-03-19','2025-11-29 16:57:52','2025-11-29 16:57:52'),(44,'Melike',NULL,'İğde','meli','5551100044',NULL,'melike.igde@gmail.com',NULL,'1998-07-31','2025-11-29 16:57:52','2025-11-29 16:57:52'),(45,'Oğuz',NULL,'Tütüncü','oguz','5551100045','5789541278','oguz.tutuncu@hotmail.com','https://www.linkedin.com/in/oguztutuncu','1992-11-06','2025-11-29 16:57:52','2025-11-29 17:12:25'),(46,'Başak',NULL,'Köroğlu','bsk','5551100046',NULL,'basak.koroglu@gmail.com','https://www.linkedin.com/in/basakkoroglu','1991-02-28','2025-11-29 16:57:52','2025-11-29 16:57:52'),(47,'Furkan',NULL,'Güçlü','furki','5551100047',NULL,'furkan.guclu@outlook.com','https://www.linkedin.com/in/furkanguclu','1996-06-10','2025-11-29 16:57:52','2025-11-29 16:57:52'),(48,'Dila',NULL,'Mülayim','dila','5551100048','536678412','dila.mulayim@yahoo.com',NULL,'1993-09-24','2025-11-29 16:57:52','2025-11-29 17:12:25'),(49,'Batı',NULL,'Sağdıç','bati','5551100049',NULL,'bati.sagdic@gmail.com','https://www.linkedin.com/in/batisagdic','1995-01-04','2025-11-29 16:57:52','2025-11-29 16:57:52'),(50,'Nuri','Ege','Göysal','gammaq','5551100050',NULL,'egegoysal@gmail.com','','1997-05-17','2025-11-29 16:57:52','2025-12-05 23:11:13');
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `surname` varchar(50) NOT NULL,
  `role` varchar(30) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `salary` double DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'tt','0e07cf830957701d43c183f1515f63e6b68027e528f43ef52b1527a520ddec82','Test','Tester','Tester','2025-11-29 15:50:56',50000),(2,'jd','ad3e69e9aa860657cc6476770fe253d08198746b9fcf9dc3186b47eb85c30335','Junior','Developer','Junior Developer','2025-11-29 15:50:56',120000),(3,'sd','03042cf8100db386818cee4ff0f2972431a62ed78edbd09ac08accfabbefd818','Senior','Developer','Senior Developer','2025-11-29 15:50:56',100000),(4,'man','ea43de53dc947fdf3cedaa4abc519f7889d5cd61f66a5ae764eb30d32c6186f9','Manager','Boss','Manager','2025-11-29 15:50:56',1000000),(5,'CapJacke','a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3','Bora','Görgün','Senior','2025-12-03 18:05:39',250000);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-06  2:22:08
