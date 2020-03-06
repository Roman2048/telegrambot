package nextg.telegrambot.domain;

import javax.persistence.*;

@Entity
@Table(name = "update")
public class Update {

    @Id
    @Column(name = "update_id")
    private Long id;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "answered")
    private Boolean answered;

    public Update() {}

    public Update(Long id, String content, String userId) {
        this.id = id;
        this.content = content;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }
}
