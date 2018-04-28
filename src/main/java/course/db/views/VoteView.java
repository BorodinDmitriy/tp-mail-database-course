package course.db.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonSubTypes;


public class VoteView implements AbstractView {
    String nickname;
    Integer vote;

    @JsonCreator
    public VoteView(@JsonProperty("nickname") String nickname,
                    @JsonProperty("voice") Integer vote) {
        this.nickname = nickname;
        this.vote = vote;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getVote() {
        return vote;
    }
}
