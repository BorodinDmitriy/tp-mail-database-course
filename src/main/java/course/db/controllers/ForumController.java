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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/forum")
public class ForumController extends AbstractController {
    @RequestMapping(path="/create", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> createForum(@RequestBody ForumView forumView) {

        long start = System.nanoTime();
        ForumModel forumModel = new ForumModel(forumView);
        StatusManagerRequest status = forumManager.create(forumModel);
        ResponseEntity m;
        switch(status.getCode()) {
            case OK:
                m =  new ResponseEntity<>(forumModel.toView(), null, HttpStatus.CREATED);
                break;
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
                break;
            case CONFILICT:
                ForumModel existingForum = new ForumModel();
                existingForum.setSlug(forumView.getSlug());
                StatusManagerRequest status1 = forumManager.findForum(existingForum);
                if (status1.getCode() == ManagerResponseCodes.DB_ERROR) {
                    m = new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                    break;
                }
                m =  new ResponseEntity<AbstractView>(existingForum.toView(), null, HttpStatus.CONFLICT);
                break;
            default:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (System.nanoTime() - start));
        return m;
    }

    @RequestMapping(path="/{slug}/details", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getBranchDetails(@PathVariable(value="slug") String slug) {
        long start = System.nanoTime();
        ForumModel forumModel = new ForumModel();
        forumModel.setSlug(slug);
        StatusManagerRequest status = forumManager.findForum(forumModel);
        ResponseEntity m;
        switch(status.getCode()) {
            case OK:
                m = new ResponseEntity<>(forumModel.toView(), null, HttpStatus.OK); //
                break;
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
                break;
            default:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                break;
        }
        //System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }

    @RequestMapping(path="/{slug}/create", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> createBranch(@PathVariable(value="slug") String slug, @RequestBody ThreadView threadView) {

        long start = System.nanoTime();
        ResponseEntity m;
        threadView.setForum(slug);
        ThreadModel threadModel = new ThreadModel(threadView);
        StatusManagerRequest status = threadManager.createThread(threadModel);
        switch(status.getCode()) {
            case OK:
                m =  new ResponseEntity<>(threadModel.toView(), null, HttpStatus.CREATED);
                break;
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
                break;
            case CONFILICT:
                ThreadModel existingThread = new ThreadModel();
                existingThread.setSlug(threadView.getSlug());
                StatusManagerRequest status1 = threadManager.findThreadBySlug(existingThread);
                if (status1.getCode() == ManagerResponseCodes.DB_ERROR) {
                    m = new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
                    break;
                }

                m = new ResponseEntity<>(existingThread.toView(), null, HttpStatus.CONFLICT);
                break;
            default:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
       // System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }

    @RequestMapping(path="/{slug}/threads", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getThreads(@PathVariable(value="slug") String slug,
                                                 @RequestParam(value="limit",required = false) Integer limit,
                                                 @RequestParam(value="since",required = false) String since,
                                                 @RequestParam(value="desc",required = false) Boolean desc
                                                 ) {
        long start = System.nanoTime();
        ResponseEntity m;
        ForumModel forumModel = new ForumModel();
        forumModel.setSlug(slug);

        List<ThreadView> threadViewList = new ArrayList<>();
        StatusManagerRequest status1 = forumManager.findThreads(forumModel, limit, since, desc, threadViewList);
        switch (status1.getCode()) {
//            cake OK:
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.NOT_FOUND);
                //System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
                return m;
            case DB_ERROR:
                m = new ResponseEntity<>(new ErrorView(status1.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
               // System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
                return m;
            default:
                break;
        }
        m = new ResponseEntity<>(threadViewList, null, HttpStatus.OK);
       // System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }

    @RequestMapping(path="/{slug}/users", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUsers(@PathVariable(value="slug") String slug,
                                                   @RequestParam(value="limit",required = false) Integer limit,
                                                   @RequestParam(value="since",required = false) String since,
                                                   @RequestParam(value="desc",required = false) Boolean desc
                                                ) {
        long start = System.nanoTime();
        ResponseEntity m;
        ForumModel forumModel = new ForumModel();
        forumModel.setSlug(slug);

        // TODO make table users forums
        List<UserProfileView> userProfileList = new ArrayList<>();
        StatusManagerRequest status = forumManager.findUsers(forumModel, limit, since, desc, userProfileList);
        switch (status.getCode()) {
            case OK:
                m = new ResponseEntity<>(userProfileList, null, HttpStatus.OK);
                break;
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
                break;
            default:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
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

