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
insert into producte(nom, calent, preu, tipus) values
("Coca Cola",0,1.50,"beguda"),
("Cervesa Corona",0,2.00,"beguda"),
("Fanta de Taronja",0,2.50,"beguda"),
("Aigua mineral",0,1.20,"beguda"),
("Vermut",0,2.80,"beguda"),
("Suc de pinya",0,2.20,"beguda"),
("Cafe",1,1.30,"beguda"),

("Patates braves",1,4.50,"tapa"),
("Croquetes de pernil",1,5.00,"tapa"),
("Calamars a la romana",1,6.50,"tapa"),
("Pernil iberic ",0,12.00,"tapa"),
("Bombes picants",1,5.50,"tapa"),
("Xampinyons a l'allet",1,4.80,"tapa"),

("Crema catalana",0,4.00,"postre"),
("Mel i mato",0,4.50,"postre"),
("Pastis de formatge",0,4.80,"postre"),
("Flam d'ou",0,3.50,"postre"),
("Brownie amb gelat",1,5.00,"postre"),
("Maduixes amb nata",0,3.50,"postre"),

("Amanida catalana",0,7.50,"entrant"),
("Sopa de galets",1,6.00,"entrant"),
("Escalivada",0,6.50,"entrant"),
("Canelons",1,8.50,"entrant"),
("Macarrons a la bolonyesa ",1,3.00,"entrant"),
("Gaspatxo",0,5.00,"entrant");

--triggers necesaris
--1 Quan es modifiqui la quantat de productes el preu conjunt es modifiqui de la taula comanda_producte
--2 Quan es faci el pagamente d'un producte el preu_restant disminueixi en funcio dels productes que ja s'han pagat
--3 Quan es pagi un producte s'actualitzi l'estat de la comanda a "efectuant_pagament" comprobant que no tingui ja aquest estat
-- a mes comprobara si s'ha pagat tots els productes de la comanda per a que quan aixo pasi, es canvii l'estat de la comanda a "pagat"
--4 Quan es faci un pagament de tota la comanda l'estat de tots els productes pasi a ser pagat

DELIMITER $$

CREATE TRIGGER actualitzar_preu_conjunt
AFTER UPDATE ON comanda_producte
FOR EACH ROW
BEGIN
	IF OLD.quantitat <> NEW.quantitat THEN
    	UPDATE comanda_producte
        SET preu_conjunt = (SELECT preu FROM producte WHERE id_producte = NEW.id_producte) * NEW.quantitat
        WHERE id_producte = NEW.id_producte and id_comanda= NEW.id_comanda;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE actualitzar_preu_restant_proc(
    IN p_id_comanda INT,
    IN p_id_producte INT,
    IN p_quantitat_pagada INT
)
BEGIN
    DECLARE p_quantitat INT;
    DECLARE p_preu DECIMAL(7,2);
    /* Obtener la cantidad total y el precio del producto*/
    SELECT quantitat INTO p_quantitat
    FROM comanda_producte
    WHERE id_comanda = p_id_comanda AND id_producte = p_id_producte;
    SELECT preu INTO p_preu
    FROM producte
    WHERE id_producte = p_id_producte;
    IF p_quantitat = p_quantitat_pagada THEN
        UPDATE comanda_producte
        SET preu_restant = 0, estat = 'pagat'
        WHERE id_comanda = p_id_comanda AND id_producte = p_id_producte;
    ELSE
        UPDATE comanda_producte
        SET preu_restant = p_preu * (p_quantitat - p_quantitat_pagada)
        WHERE id_comanda = p_id_comanda AND id_producte = p_id_producte;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER actualitzar_estat_comanda
AFTER UPDATE ON comanda_producte
FOR EACH ROW
BEGIN
DECLARE resultats INT;
    IF OLD.quantitat_pagada <> NEW.quantitat_pagada THEN
        IF (SELECT estat FROM comanda WHERE id_comanda = NEW.id_comanda) != 'efectuant_pagament' THEN
            UPDATE comanda
            SET estat = 'efectuant_pagament'
            WHERE id_comanda = NEW.id_comanda;
        END IF;
    END IF;
    IF OLD.estat <> NEW.estat THEN
        SELECT COUNT(*)
        INTO resultats
        FROM comanda_producte
        WHERE id_comanda = NEW.id_comanda AND estat != 'pagat';
        IF resultats = 0 THEN
            UPDATE comanda
            SET estat = 'pagat'
            WHERE id_comanda = NEW.id_comanda;
        END IF;
    END IF;
END$$

DELIMITER ;

DELIMITER $$
CREATE PROCEDURE p_pagament_total(
    IN p_id_comanda INT
)
BEGIN
    DECLARE p_id_producte INT;
    DECLARE p_quantitat INT;
    DECLARE done INT DEFAULT 0;
    DECLARE c_coman_prod CURSOR FOR
        SELECT id_producte, quantitat from comanda_producte where id_comanda = p_id_comanda;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    UPDATE comanda_producte
    SET quantitat_pagada = quantitat
    WHERE id_comanda = p_id_comanda;

    UPDATE comanda SET estat = 'pagat' WHERE id_comanda=p_id_comanda;
    open c_coman_prod;
    bucle_productos: LOOP
        FETCH c_coman_prod into p_id_producte,p_quantitat;
        IF done then
            LEAVE bucle_productos;
        END IF;
        CALL actualizar_preu_restant_proc(p_id_comanda,p_id_producte,p_quantitat);
    END LOOP;
    CLOSE c_coman_prod;
END$$

DELIMITER ;