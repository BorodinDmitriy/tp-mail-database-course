package course.db.dao;

import course.db.db_queries.QueryForForums;
import course.db.db_queries.QueryForPost;
import course.db.db_queries.QueryForThread;
import course.db.db_queries.QueryForUserProfile;
import course.db.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

@Repository
public class PostDAO extends AbstractDAO {
    @NotNull
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PostModel findById(Integer id) {
        PostModel postModel = jdbcTemplate.queryForObject(QueryForPost.getById(), new Object[] {id}, _getPostModel);
        return postModel;
    }

    private void fillStatment(PreparedStatement statement, Integer id, Integer a_id, Integer t_id, Timestamp created,
                              Integer f_id, PostModel model) throws SQLException {
        statement.setInt(1, id);  // id
        statement.setInt(2, model.getParent());  // parent_id
        statement.setInt(3, a_id); //author_id
        statement.setTimestamp(4, created); // crecreated
        statement.setInt(5, f_id);  // forumId
        statement.setString(6, model.getMessage()); // message
        statement.setInt(7, t_id);
        if (model.getParent() == 0) {
            statement.setInt(8, id);    // id_of_root
            statement.setArray(9, null);    // arr
            statement.setInt(10, id);   // arr
        }
        else {
            Array pathArray = jdbcTemplate.queryForObject(QueryForPost.getPath(), new Object[] {model.getParent()}, Array.class);
            statement.setInt(8, ((Integer[])pathArray.getArray())[0]); // id
            statement.setArray(9, pathArray); //arr
            statement.setInt(10, id); // arr
        }
    }

    public List<PostModel> findSorted(ThreadModel threadModel, Integer limit, Integer since, String sort, Boolean desc) {
        if (sort == null)
            sort = "flat";
        switch (sort) {
            case "tree":
                return findSortedTree(threadModel.getId(), limit, since, desc);
            case "parent_tree":
                return findSortedParentTree(threadModel.getId(), limit, since, desc);
            default:
                return findSortedFlat(threadModel.getId(), limit, since, desc);
        }
        /* String SQL="SELECT * FROM \"posts\" WHERE thread_id=? ";
        List<Object> lst= new LinkedList<>();
        lst.add(threadModel.getId());


        if (sort!=null) {
            if(sort.equals("flat")) {
                if (since!=null)
                {
                    if (desc != null && desc == true )
                    {
                        SQL+= " AND id<? ";
                    }
                    else {
                        SQL += " AND id>? ";
                    }
                    lst.add(since);
                }
                SQL += " ORDER BY ";
                SQL += " created ";
                if (desc != null && desc.equals(true))
                {
                    SQL+=" DESC ";
                }
                SQL += " ,id   ";
            }

            if(sort.equals("tree")){
                if (since != null) {
                    if (desc != null && desc.equals(true)) {
                        SQL += " AND path_to_post < (SELECT path_to_post FROM posts WHERE id = ?) ";

                    }
                    else{
                        SQL += " AND path_to_post > (SELECT path_to_post FROM posts WHERE id = ?) ";
                    }
                    lst.add(since);
                }
                SQL += " ORDER BY path_to_post";
            }

            if(sort.equals("parent_tree")){
                SQL="SELECT * FROM  posts WHERE ";
                lst.clear();
                lst.add(threadModel.getId());
                if(limit!=null){
                    SQL+=" path_to_post[1] IN (SELECT DISTINCT path_to_post[1] FROM" +
                            " posts WHERE thread_id=? ";
                    if(since != null)
                    {
                        if (desc != null && desc.equals(true)) {
                            SQL += " AND path_to_post[1] < (SELECT path_to_post[1] FROM posts WHERE id = ?) ";

                        }
                        else{
                            SQL += " AND path_to_post[1] > (SELECT path_to_post[1] FROM posts WHERE id = ?) ";
                        }
                        lst.add(since);
                    }

                    SQL+=" ORDER BY path_to_post[1] ";
                    if (desc != null && desc.equals(true)) {
                        SQL += " DESC ";

                    }
                    SQL+=" LIMIT ?) ";

                    lst.add(limit);
                }
                else {
                    SQL+=" thread_id=? ;";
                }
                SQL += " ORDER BY path_to_post[1] ";
                if (desc != null && desc.equals(true))
                {
                    SQL+=" DESC ";
                }


                SQL+=",path_to_post,id";
                SQL+=";";
                return jdbcTemplate.query(SQL,_getPostModel,lst.toArray());
            }
        }
        if(sort==null)
        {
            if (since!=null)
            {
                if (desc != null && desc == true )
                {
                    SQL+= " AND id<? ";
                }
                else {
                    SQL += " AND id>? ";
                }
                lst.add(since);
            }
            SQL+=" ORDER BY ID";
        }


        if (desc != null && desc == true )
        {
            SQL+=" DESC ";
        }

        if(limit!=null)
        {
            SQL+=" LIMIT ? ";
            lst.add(limit);
        }

        SQL+=";";
        return jdbcTemplate.query(SQL,_getPostModel,lst.toArray());
*/
    }

    public List<PostModel> findSortedFlat(Integer thread_id, Integer limit, Integer since, Boolean desc) {
        final StringBuilder SQL = new StringBuilder();
        final ArrayList<Object> lst = new ArrayList<>();
        SQL.append("SELECT p.author_name, p.created, f.slug, p.forum_slug, p.id, p.is_edited, p.message, p.parent_id, p.thread_id " +
                " FROM posts p JOIN forums f on p.forum_id = f.id WHERE thread_id = ? ");
        lst.add(thread_id);

        if (since != null) {
            SQL.append(" AND p.id " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                SQL.append(" < ? ");
            } else {
                SQL.append(" > ? ");
            }

            lst.add(since);
        }

        SQL.append(" ORDER BY (p.id) ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            SQL.append(" DESC ");
        }

        if (limit != null) {
            SQL.append(" LIMIT ? ");
            lst.add(limit);
        }

        return jdbcTemplate.query(SQL.toString(), _getPostModel, lst.toArray());
    }

    public List<PostModel> findSortedTree(Integer thread_id, Integer limit, Integer since, Boolean desc) {
        final StringBuilder SQL = new StringBuilder();
        final ArrayList<Object> lst = new ArrayList<>();
        SQL.append("SELECT p.author_name, p.created, f.slug, p.forum_slug, p.id, p.is_edited, p.message, p.parent_id, p.thread_id " +
                " FROM posts p JOIN forums f on p.forum_id = f.id WHERE thread_id = ? ");
        lst.add(thread_id);

        if (since != null) {
            SQL.append(" AND p.path_to_post " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                SQL.append(" < ");
            } else {
                SQL.append(" > ");
            }

            SQL.append(" (SELECT path_to_post from posts WHERE id = ?) ");
            lst.add(since);
        }

        SQL.append(" ORDER BY p.path_to_post ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            SQL.append(" DESC ");
        }

        if (limit != null) {
            SQL.append(" LIMIT ? ");
            lst.add(limit);
        }

        return jdbcTemplate.query(SQL.toString(), _getPostModel, lst.toArray());
    }

    public List<PostModel> findSortedParentTree(Integer thread_id, Integer limit, Integer since, Boolean desc) {
        final StringBuilder SQL = new StringBuilder();
        final ArrayList<Object> lst = new ArrayList<>();
        SQL.append("SELECT p.author_name, p.created, f.slug, p.forum_slug, p.id, p.is_edited, p.message, p.parent_id, p.thread_id " +
                " FROM posts p JOIN forums f on p.forum_id = f.id " +
                " WHERE p.id_of_root IN ( SELECT id FROM posts WHERE thread_id = ? AND parent_id = 0 ");
        lst.add(thread_id);

        if (since != null) {
            SQL.append(" AND id " );

            if (desc != null && desc.equals(Boolean.TRUE)) {
                SQL.append(" < ");
            } else {
                SQL.append(" > ");
            }

            SQL.append(" (SELECT id_of_root from posts WHERE id = ?) ");
            lst.add(since);
        }

        SQL.append(" ORDER BY id ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            SQL.append(" DESC ");
        }

        if (limit != null) {
            SQL.append(" LIMIT ? ");
            lst.add(limit);
        }

        SQL.append(" ) ORDER BY ");
        if (desc != null && desc.equals(Boolean.TRUE)) {
            SQL.append(" p.id_of_root DESC, ");
        }
        SQL.append(" p.path_to_post ");

        return jdbcTemplate.query(SQL.toString(), _getPostModel, lst.toArray());
    }

    public void createByThreadIdOrSlug(List<PostModel> postModelList, ThreadModel threadModel) throws DataAccessException {
        Integer threadId;
        if (threadModel.getId() != null)
            threadId = threadModel.getId();
        else
            threadId = jdbcTemplate.queryForObject(QueryForThread.findThreadIdBySlug(), new Object[] {threadModel.getSlug()}, Integer.class);

        Integer forumId = jdbcTemplate.queryForObject(QueryForThread.findForum(), new Object[] {threadId}, Integer.class);
        Timestamp created = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Connection conn = null;
        PreparedStatement createPost = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try {
                createPost = conn.prepareStatement(QueryForPost.createPost());
                for (PostModel postModel : postModelList) {
                    Integer userId = jdbcTemplate.queryForObject(QueryForUserProfile.getIdByNick(), new Object[]{postModel.getAuthor()}, Integer.class);
                    Integer postId = jdbcTemplate.queryForObject(QueryForPost.getId(), Integer.class);
                    fillStatment(createPost, postId, userId, threadId, created, forumId, postModel);

                    createPost.addBatch();
                    postModel.setCreated(dateFormat.format(created));
                    postModel.setId(postId);
                }
                createPost.executeBatch();
                conn.commit();
            }
            catch (Exception ex) {
                conn.rollback();
                throw new DataRetrievalFailureException(ex.getLocalizedMessage());
            }
            finally {
                if (createPost != null)
                    createPost.close();
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException ex) {
            throw new DataRetrievalFailureException(ex.getLocalizedMessage());
        }
        finally {
            try {
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                throw new DataRetrievalFailureException(e.getLocalizedMessage());
            }
        }
        jdbcTemplate.update(QueryForThread.updatePostCount(), new Object[] { postModelList.size(), forumId});
    }

    public PostDetailsModel getDetails(Integer id, String[] args) {
        PostModel postModel = jdbcTemplate.queryForObject(QueryForPost.getById(), new Object[] {id}, _getPostModel);
        UserProfileModel userProfileModel = null;
        ThreadModel threadModel = null;
        ForumModel forumModel = null;

        if (args != null) {
            for (String arg : args) {
                if (arg.equals("user")) {
                    userProfileModel = jdbcTemplate.queryForObject(QueryForUserProfile.getUserByNickOrEmail(),
                            new Object[] {postModel.getAuthor(), null}, _getUserModel);
                } else if (arg.equals("thread")) {
                    threadModel = jdbcTemplate.queryForObject(QueryForThread.findThreadById(), new
                            Object[] {postModel.getThread()}, _getThreadModel);
                } else if (arg.equals("forum")) {
                    forumModel = jdbcTemplate.queryForObject(QueryForForums.findForumBySlug(),
                            new Object[] {postModel.getForum()}, _getForumModel);
                }
            }
        }
        return new PostDetailsModel(userProfileModel, postModel, threadModel, forumModel);
    }

    public PostModel updatePost(PostModel newPostModel) {
        PostModel oldPostModel = jdbcTemplate.queryForObject(QueryForPost.getById(), new Object[] {newPostModel.getId()}, _getPostModel);
        StringBuilder builder = new StringBuilder("UPDATE posts SET message = ?");
        if (!(newPostModel.getMessage()).equals(oldPostModel.getMessage())) {
            builder.append(", is_edited = TRUE");
            oldPostModel.setEdited(true);
            oldPostModel.setMessage(newPostModel.getMessage());
        }
        builder.append(" WHERE id = ?");
        jdbcTemplate.update(builder.toString(), newPostModel.getMessage(), newPostModel.getId());
        return oldPostModel;
    }

    @Override
    public Integer count() {
        return jdbcTemplate.queryForObject(QueryForPost.count(), Integer.class);
    }

    @Override
    public void clear() {
        jdbcTemplate.execute(QueryForPost.clear());
    }
}
