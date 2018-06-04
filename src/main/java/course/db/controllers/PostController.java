package course.db.controllers;

import course.db.managers.StatusManagerRequest;
import course.db.models.PostDetailsModel;
import course.db.models.PostModel;
import course.db.views.AbstractView;
import course.db.views.ErrorView;
import course.db.views.PostView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(path="/api/post")
public class PostController extends AbstractController {
    @RequestMapping(path="/{id}/details", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getDetails(@PathVariable(value="id") Integer id,
                                                   @RequestParam(value="related", required = false) String[] related) {
        long start = System.nanoTime();
        ResponseEntity m;
        PostDetailsModel postDetailsModel = new PostDetailsModel();
        StatusManagerRequest status = postManager.findPostDetailsById(id, related, postDetailsModel);
        switch(status.getCode()) {
            case OK:
                m = new ResponseEntity<>(postDetailsModel.toView(), null, HttpStatus.OK); //
                break;
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
                break;
            default:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
       // System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }

    @RequestMapping(path="/{id}/details", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> setDetails(@PathVariable(value="id") String id, @RequestBody PostView postView) {
        long start = System.nanoTime();
        ResponseEntity m;
        PostModel postModel = new PostModel(postView);
        postModel.setId(new Integer(id));
        StatusManagerRequest status = postManager.updatePost(postModel);
        switch(status.getCode()) {
            case OK:
                m = new ResponseEntity<>(postModel.toView(), null, HttpStatus.OK); //
                break;
            case NO_RESULT:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
                break;
            default:
                m = new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
       // System.out.println(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + " " + (double)(System.nanoTime() - start) / 1000000000.0);
        return m;
    }
}
