DELIMITER //
CREATE PROCEDURE insert_sales_transactions(IN email VARCHAR(50), IN token VARCHAR(50))
proc: begin
    DECLARE done INT DEFAULT FALSE;
    DECLARE EMAIL2 VARCHAR(50);
    DECLARE MOVIEID2 VARCHAR(10);
    DECLARE QUANTITY2 INT;
    DECLARE COUNT2 INT;
    DECLARE c1 CURSOR FOR
        SELECT customers.email, movieId, quantity
        FROM customers LEFT JOIN carts ON customers.email = carts.email
        WHERE customers.email = email;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    open c1;
    SET COUNT2 = 0;

    read_loop: LOOP
        fetch c1 into EMAIL2, MOVIEID2, QUANTITY2;
        IF done THEN
            LEAVE read_loop;
        END IF;
        IF MOVIEID2 is null THEN
            LEAVE proc;
        end if;
        SET COUNT2 = COUNT2 + 1;
        INSERT INTO sales (email, movieId, quantity, saleDate) VALUES (EMAIL2, MOVIEID2, QUANTITY2, CURRENT_DATE);
        INSERT INTO transactions (sId, token) VALUES (LAST_INSERT_ID(), token);
        DELETE FROM carts WHERE carts.email = email;
    end loop;

    IF COUNT2 = 0 THEN
        LEAVE proc;
    end if;

    close c1;
end //
DELIMITER ;