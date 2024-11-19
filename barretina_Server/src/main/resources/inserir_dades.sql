use barretina;
insert into cambrer(nom) VALUES ("Pepe"),("Juan"),("Pedro");

DELIMITER $$

CREATE PROCEDURE insertTaules (inici INT, final INT)
BEGIN
    DECLARE c INT DEFAULT 0;
    DECLARE idcambrer INT;

    SET c = inici;
    SELECT id_cambrer INTO idcambrer FROM cambrer ORDER BY id_cambrer DESC LIMIT 1;

    WHILE c <= final DO
        INSERT INTO taula (id_cambrer, ocupada) VALUES (idcambrer, FALSE);
        SET c = c + 1;
    END WHILE;
END$$

DELIMITER ;

