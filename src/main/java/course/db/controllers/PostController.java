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

@RestController
@RequestMapping(path="/api/post")
public class PostController extends AbstractController {
    @RequestMapping(path="/{id}/details", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> getDetails(@PathVariable(value="id") Integer id,
                                                   @RequestParam(value="related", required = false) String[] related) {
        PostDetailsModel postDetailsModel = new PostDetailsModel();
        StatusManagerRequest status = postManager.findPostDetailsById(id, related, postDetailsModel);
        switch(status.getCode()) {
            case OK:
                return new ResponseEntity<>(postDetailsModel.toView(), null, HttpStatus.OK); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/{id}/details", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbstractView> setDetails(@PathVariable(value="id") String id, @RequestBody PostView postView) {
        PostModel postModel = new PostModel(postView);
        postModel.setId(new Integer(id));
        StatusManagerRequest status = postManager.updatePost(postModel);
        switch(status.getCode()) {
            case OK:
                return new ResponseEntity<>(postModel.toView(), null, HttpStatus.OK); //
            case NO_RESULT:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(new ErrorView(status.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
