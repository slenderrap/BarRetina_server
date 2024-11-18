insert into cambrer(nom) VALUES (("Pepe"),("Juan"),("Pedro"));

Create procedure insertTaules (inici int , final int)
    Begin 
        DECLARE c int default inici;
        DECLARE idcambrer int;
        SELECT id_cambrer INTO idcambrer FROM cambrer ORDER BY id_cambrer DESC LIMIT 1; 
        
        WHILE c <= final DO
            insert into taula (id_cambrer,ocupada) VALUES (idcambrer,FALSE);
            set c = c + 1;
        END WHILE; 
    END;