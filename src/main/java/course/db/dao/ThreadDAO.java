package course.db.dao;

import course.db.db_queries.QueryForForums;
import course.db.db_queries.QueryForPost;
import course.db.db_queries.QueryForThread;
import course.db.db_queries.QueryForUserProfile;
import course.db.models.ThreadModel;
import course.db.models.VoteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ThreadDAO extends AbstractDAO {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ThreadModel createThread(ThreadModel threadModel) {
        Integer userId = jdbcTemplate.queryForObject(QueryForUserProfile.getIdByNick(), new Object[] {threadModel.getAuthor()},
                Integer.class);

        Integer forumId = jdbcTemplate.queryForObject(QueryForForums.findForumIdBySlug(), new Object[] {threadModel.getForum()},
                Integer.class);

        String created = threadModel.getCreated();
        Integer id;
        if (created == null) {
            id = jdbcTemplate.queryForObject(QueryForThread.createNoDate(), new Object[] {
                    userId, forumId, threadModel.getTitle(), threadModel.getMessage(), 0, threadModel.getSlug()},
                    Integer.class);
        }
        else
        {
            id = jdbcTemplate.queryForObject(QueryForThread.createWithDate(), new Object[] {
                    userId, forumId, threadModel.getTitle(), threadModel.getCreated(), threadModel.getMessage(), 0, threadModel.getSlug()}, Integer.class);
        }

//        jdbcTemplate.update(QueryForForums.addUser(), new Object[] {userId, forumId});

        return jdbcTemplate.queryForObject(QueryForThread.findThreadById(), new Object[] {id}, _getThreadModel);
    }

    public void updateThread(ThreadModel threadModel) {
        StringBuilder builder = new StringBuilder("UPDATE threads SET");
        List<Object> args = new ArrayList<>();

        if (threadModel.getMessage() != null) {
            builder.append(" message = ?,");
            args.add(threadModel.getMessage());
        }

        if (threadModel.getTitle() != null) {
            builder.append(" title = ?,");
            args.add(threadModel.getTitle());
        }

        if (!args.isEmpty()) {
            builder.delete(builder.length() - 1, builder.length());
            if (threadModel.getId() != null) {
                builder.append(" WHERE id = ?");
                args.add(threadModel.getId());
            }
            else {
                builder.append(" WHERE slug = ?::CITEXT");
                args.add(threadModel.getSlug());
            }
            jdbcTemplate.update(builder.toString(), args.toArray());
        }
    }

    public ThreadModel setVote(VoteModel voteModel, ThreadModel threadModel) {
        Integer userId = jdbcTemplate.queryForObject(QueryForUserProfile.getIdByNick(), Integer.class, voteModel.getNickname());
        Connection conn = null;
        CallableStatement callableStatement = null;

        Integer threadId;
        if (threadModel.getId() != null) {
            threadId = threadModel.getId();
        } else
            threadId = jdbcTemplate.queryForObject(QueryForThread.findThreadIdBySlug(), new Object[]{threadModel.getSlug()}, Integer.class);

        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try {
                callableStatement = conn.prepareCall(QueryForThread.createOrUpdateVote());
                callableStatement.setInt(1, userId);
                callableStatement.setInt(2, threadId);
                callableStatement.setInt(3, voteModel.getVote());
                callableStatement.execute();
                conn.setAutoCommit(true);
            } catch (SQLException sEx) {
                conn.rollback();
                throw new DataRetrievalFailureException(sEx.getLocalizedMessage());
            } finally {
                if (callableStatement != null)
                    callableStatement.close();
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new DataRetrievalFailureException(ex.getLocalizedMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                throw new DataRetrievalFailureException(e.getLocalizedMessage());
            }
        }

        return jdbcTemplate.queryForObject(QueryForThread.findThreadById(), new Object[] {threadId}, _getThreadModel);
    }



        //TODO check thread

    public ThreadModel oldsetVote(VoteModel voteModel, ThreadModel threadModel) {
        Integer userId = jdbcTemplate.queryForObject(QueryForUserProfile.getIdByNick(), Integer.class, voteModel.getNickname());
        Integer threadId;
        if (threadModel.getId() != null) {
            threadId = threadModel.getId();
        }
        else
            threadId = jdbcTemplate.queryForObject(QueryForThread.findThreadIdBySlug(), new Object[] {threadModel.getSlug()}, Integer.class);
        try {
            jdbcTemplate.update(QueryForThread.insertVote(), new Object[] {userId, threadId, voteModel.getVote()});
        }
        catch (DuplicateKeyException ex) {
            jdbcTemplate.update(QueryForThread.updateVote(), new Object[] {voteModel.getVote(), userId, threadId});
        }
        Integer vote_sum = jdbcTemplate.queryForObject(QueryForThread.getVoteSum(), new Object[] {threadId}, Integer.class);

        jdbcTemplate.update(QueryForThread.updateVotes(), new Object[] {vote_sum, threadId});
        return jdbcTemplate.queryForObject(QueryForThread.findThreadById(), new Object[] {threadId}, _getThreadModel);
    }

    public ThreadModel findBySlugOrId(ThreadModel threadModel) {
        if (threadModel.getId() != null)
            return jdbcTemplate.queryForObject(QueryForThread.findThreadById(), new Object[]
                {threadModel.getId()}, _getThreadModel);
        else
            return jdbcTemplate.queryForObject(QueryForThread.findThreadBySlug(), new Object[]
                    {threadModel.getSlug()}, _getThreadModel);
    }

    public ThreadModel findThread(ThreadModel threadModel) {
        return jdbcTemplate.queryForObject(QueryForForums.findThreads(), new Object[] {threadModel.getSlug()}, _getThreadModel);
    }

    @Override
    public Integer count() {
        return jdbcTemplate.queryForObject(QueryForThread.count(), Integer.class);
    }

    @Override
    public void clear(){
        jdbcTemplate.execute(QueryForThread.clear());
    }
}
