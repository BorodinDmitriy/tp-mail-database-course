---------------------- vote -----------------------------------------
CREATE OR REPLACE FUNCTION create_or_update_vote(u_id integer, t_id integer, v integer)
  RETURNS INTEGER as '
  DECLARE
    flag integer;
  BEGIN
    select 1 from votes where owner_id = u_id and thread_id = t_id into flag;
    IF flag = 1 THEN
      UPDATE votes SET vote = v WHERE owner_id = u_id and thread_id = t_id;
    ELSE
      INSERT into votes(owner_id, thread_id, vote) VALUES(u_id, t_id, v);
    END IF;
    UPDATE threads set votes = (SELECT SUM(vote) FROM votes WHERE thread_id = t_id);
    RETURN 1;
   END;'
LANGUAGE plpgsql;
