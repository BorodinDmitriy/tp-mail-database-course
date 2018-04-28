package course.db.controllers;

import course.db.managers.StatusManagerRequest;
import course.db.models.PostModel;
import course.db.models.ThreadModel;
import course.db.models.VoteModel;
import course.db.views.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/thread")
public class ThreadController extends AbstractController {
    private void checkAndSetSlugOrId(String slug_or_id, ThreadModel threadModel) {
        try {
            int id = new Integer(slug_or_id);
            threadModel.setId(id);
        } catch (NumberFormatException e) {
            threadModel.setSlug(slug_or_id);
        }
    }

    @RequestMapping(path="/{slug_or_id}/create", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createPosts(@RequestBody ArrayList<PostView> posts,
                                                     @PathVariable(value = "slug_or_id") String slug_or_id) {
        final ThreadModel threadModel = new ThreadModel();
        checkAndSetSlugOrId(slug_or_id, threadModel);

        StatusManagerRequest status = threadManager.findThreadBySlugOrId(threadModel);
        switch (status.getCode()) {
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            case DB_ERROR:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (posts.isEmpty()) {
            return new ResponseEntity<>(posts, null, HttpStatus.CREATED);
        }

        List<PostModel> postModelList = new ArrayList<>();

        for (PostView post : posts) {
            if (post.getParent() != 0) {
                PostModel prnt = new PostModel();
                StatusManagerRequest status1 = postManager.findById(post.getParent(), prnt);

                switch (status1.getCode()) {
                    case NO_RESULT:
                        return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.CONFLICT);
                    case DB_ERROR:
                        return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                post.setForum(threadModel.getForum());
                post.setThread(threadModel.getId());

                if (!threadModel.getId().equals(prnt.getThread())) {
                    return new ResponseEntity<>(new ErrorView("not equals id"), null, HttpStatus.CONFLICT);
                }
            }
            post.setForum(threadModel.getForum());
            post.setThread(threadModel.getId());
            postModelList.add(new PostModel(post));
        }

        StatusManagerRequest status2 = postManager.create(postModelList, threadModel);

        switch (status2.getCode()) {
            case CONFILICT:
                return new ResponseEntity<>(new ErrorView(status2.getMessage()), null, HttpStatus.CONFLICT);
            case DB_ERROR:
                return new ResponseEntity<>(new ErrorView(status2.getMessage()), null, HttpStatus.NOT_FOUND);
        }
        List<PostView> postViews = new ArrayList<>();
        for (PostModel postModel: postModelList) {
            postViews.add(postModel.toView());
        }
        return new ResponseEntity<>(postViews, null, HttpStatus.CREATED);
    }



    @RequestMapping(path="/{slug_or_id}/details", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getDetails(@PathVariable(value = "slug_or_id") String slug_or_id) {

        final ThreadModel threadModel = new ThreadModel();
        checkAndSetSlugOrId(slug_or_id, threadModel);
        StatusManagerRequest status = threadManager.findThreadBySlugOrId(threadModel);

        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(threadModel.toView(), null, HttpStatus.OK); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND); //
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND); //
        }
    }

    @RequestMapping(path="/{slug_or_id}/details", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> setDetails(@RequestBody ThreadView threadView,
                                                   @PathVariable(value = "slug_or_id") String slug_or_id) {
        final ThreadModel threadModel = new ThreadModel(threadView);
        checkAndSetSlugOrId(slug_or_id, threadModel);

        StatusManagerRequest status = threadManager.updateThread(threadModel);
        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(threadModel.toView(), null, HttpStatus.OK); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND); //
            case CONFILICT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.CONFLICT); //
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR); //
        }

    }

    @RequestMapping(path="/{slug_or_id}/posts", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPosts(@PathVariable(value = "slug_or_id") String slug_or_id,
                                                 @RequestParam(value="limit",required = false) Integer limit,
                                                 @RequestParam(value="since",required = false) Integer since,
                                                 @RequestParam(value="sort",required = false) String sort,
                                                 @RequestParam(value="desc",required = false) Boolean desc) {
        ThreadModel threadModel = new  ThreadModel();
        checkAndSetSlugOrId(slug_or_id, threadModel);
        StatusManagerRequest status = threadManager.findThreadBySlugOrId(threadModel);

        switch (status.getCode()) {
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            case DB_ERROR:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        List<PostModel> postModels =  new ArrayList<>();

        StatusManagerRequest status1 = postManager.findSorted(postModels, threadModel, limit, since, sort, desc);

        switch (status1.getCode()) {
            case OK:
                List<PostView> postViews = new ArrayList<>();
                for (PostModel model : postModels) {
                    postViews.add(model.toView());
                }
                return new ResponseEntity<>(postViews, null, HttpStatus.OK); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.NOT_FOUND); //
            default:
                return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR); //
        }
    }

    
    @RequestMapping(path="/{slug_or_id}/vote", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> voteThread(@RequestBody VoteView voteView, @PathVariable String slug_or_id) {

        ThreadModel threadModel = new  ThreadModel();
        checkAndSetSlugOrId(slug_or_id, threadModel);

        StatusManagerRequest status = threadManager.setVote(new VoteModel(voteView), threadModel);
        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(threadModel.toView(), null, HttpStatus.OK);
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND); //
            case CONFILICT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.CONFLICT); //
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR); //
        }
    }
}
