package course.db.controllers;

import course.db.managers.ManagerResponseCodes;
import course.db.managers.StatusManagerRequest;
import course.db.models.ForumModel;
import course.db.models.ThreadModel;
import course.db.views.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/forum")
public class ForumController extends AbstractController {
    @RequestMapping(path="/create", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> createForum(@RequestBody ForumView forumView) {

        ForumModel forumModel = new ForumModel(forumView);
        StatusManagerRequest status = forumManager.create(forumModel);
        switch(status.getCode()) {
            case OK:
                return new ResponseEntity<>(forumModel.toView(), null, HttpStatus.CREATED); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            case CONFILICT:
                ForumModel existingForum = new ForumModel();
                existingForum.setSlug(forumView.getSlug());
                StatusManagerRequest status1 = forumManager.findForum(existingForum);
                if (status1.getCode() == ManagerResponseCodes.DB_ERROR)
                    return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<AbstractView>(existingForum.toView(), null, HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/{slug}/details", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getBranchDetails(@PathVariable(value="slug") String slug) {
        ForumModel forumModel = new ForumModel();
        forumModel.setSlug(slug);
        StatusManagerRequest status = forumManager.findForum(forumModel);
        switch(status.getCode()) {
            case OK:
                return new ResponseEntity<>(forumModel.toView(), null, HttpStatus.OK); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/{slug}/create", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> createBranch(@PathVariable(value="slug") String slug, @RequestBody ThreadView threadView) {

        threadView.setForum(slug);
        ThreadModel threadModel = new ThreadModel(threadView);
        StatusManagerRequest status = threadManager.createThread(threadModel);
        switch(status.getCode()) {
            case OK:
                return new ResponseEntity<>(threadModel.toView(), null, HttpStatus.CREATED); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            case CONFILICT:
                ThreadModel existingThread = new ThreadModel();
                existingThread.setSlug(threadView.getSlug());
                StatusManagerRequest status1 = threadManager.findThreadBySlug(existingThread);
                if (status1.getCode() == ManagerResponseCodes.DB_ERROR)
                    return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(existingThread.toView(), null, HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/{slug}/threads", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getThreads(@PathVariable(value="slug") String slug,
                                                 @RequestParam(value="limit",required = false) Integer limit,
                                                 @RequestParam(value="since",required = false) String since,
                                                 @RequestParam(value="desc",required = false) Boolean desc
                                                 ) {
        ForumModel forumModel = new ForumModel();
        forumModel.setSlug(slug);

        List<ThreadView> threadViewList = new ArrayList<>();
        StatusManagerRequest status1 = forumManager.findThreads(forumModel, limit, since, desc, threadViewList);
        switch (status1.getCode()) {
//            cake OK:
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.NOT_FOUND);
            case DB_ERROR:
                return new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
            default:
                break;
        }
        return new ResponseEntity<>(threadViewList, null, HttpStatus.OK);

    }

    @RequestMapping(path="/{slug}/users", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUsers(@PathVariable(value="slug") String slug,
                                                   @RequestParam(value="limit",required = false) Integer limit,
                                                   @RequestParam(value="since",required = false) String since,
                                                   @RequestParam(value="desc",required = false) Boolean desc
                                                ) {
        ForumModel forumModel = new ForumModel();
        forumModel.setSlug(slug);

        // TODO make table users forums
        List<UserProfileView> userProfileList = new ArrayList<>();
        StatusManagerRequest status = forumManager.findUsers(forumModel, limit, since, desc, userProfileList);
        switch (status.getCode()) {
            case OK:
                return new ResponseEntity<>(userProfileList, null, HttpStatus.OK);
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


//        StatusManagerRequest status = forumManager.findForum(forumModel);
//        switch (status.getCode()) {
//            case NO_RESULT:
//                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
//            case DB_ERROR:
//                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
//            default:
//                break;
//        }

//        StatusManagerRequest status = forumManager.findForum(forumModel);
//        switch (status.getCode()) {
//            case NO_RESULT:
//                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
//            case DB_ERROR:
//                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
//            default:
//                break;
//        }

