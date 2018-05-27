--------------------- TRIGGER FOR UPDATE forums ------------------------
CREATE OR REPLACE FUNCTION insert_forum_func() RETURNS TRIGGER AS
$insert_forums_trigger$
  BEGIN
    UPDATE forums SET owner_name = (SELECT nickname FROM userprofiles WHERE id = NEW.owner_id)
      WHERE id = NEW.id;
    RETURN NULL;
  END;
$insert_forums_trigger$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_forums_trigger ON forums;
CREATE TRIGGER insert_forums_trigger AFTER INSERT ON forums
  FOR EACH ROW EXECUTE PROCEDURE insert_forum_func();
---------------------------------------------------------------------



------------------- TRIGGER FOR UPDATE threads ---------------
CREATE OR REPLACE FUNCTION insert_threads_func() RETURNS TRIGGER AS
$insert_threads_trigger$
  BEGIN
      UPDATE threads SET author_name = (SELECT nickname FROM userprofiles WHERE id = NEW.author_id),
                      forum_slug = (SELECT slug FROM forums WHERE id = NEW.forum_id)
      WHERE id = NEW.id;
      UPDATE forums SET threads = threads + 1 WHERE id = NEW.forum_id;
       INSERT INTO forums_and_users(user_id, forum_id) VALUES(NEW.author_id, NEW.forum_id);
    RETURN NULL;
  END;
$insert_threads_trigger$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_threads_trigger ON threads;
CREATE TRIGGER insert_threads_trigger AFTER INSERT ON threads
  FOR EACH ROW EXECUTE PROCEDURE insert_threads_func();

-------------------------------------------------------------------


------------------- TRIGGER FOR UPDATE posts ------------------------

CREATE OR REPLACE FUNCTION insert_posts_func() RETURNS TRIGGER AS
$insert_posts_trigger$
  BEGIN
      UPDATE posts SET author_name = (SELECT nickname FROM userprofiles WHERE id = NEW.author_id),
                      forum_slug = (SELECT slug FROM forums WHERE id = NEW.forum_id)
      WHERE id = NEW.id;
      INSERT INTO forums_and_users(user_id, forum_id) VALUES(NEW.author_id, NEW.forum_id);
    RETURN NULL;
  END;
$insert_posts_trigger$ LANGUAGE plpgsql;
--
DROP TRIGGER IF EXISTS insert_posts_trigger ON posts;
CREATE TRIGGER insert_posts_trigger AFTER INSERT ON posts
  FOR EACH ROW EXECUTE PROCEDURE insert_posts_func();
