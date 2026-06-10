package cn.campushub.model;

import java.time.LocalDateTime;

public class ClaimRequest {
    private Long id;
    private Long lostFoundId;
    private Long userId;
    private String message;
    private String contact;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
    private String applicantNickname;
    private String applicantAvatar;
    private String applicantCollege;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLostFoundId() { return lostFoundId; }
    public void setLostFoundId(Long lostFoundId) { this.lostFoundId = lostFoundId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getHandledAt() { return handledAt; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
    public String getApplicantNickname() { return applicantNickname; }
    public void setApplicantNickname(String applicantNickname) {
        this.applicantNickname = applicantNickname;
    }
    public String getApplicantAvatar() { return applicantAvatar; }
    public void setApplicantAvatar(String applicantAvatar) {
        this.applicantAvatar = applicantAvatar;
    }
    public String getApplicantCollege() { return applicantCollege; }
    public void setApplicantCollege(String applicantCollege) {
        this.applicantCollege = applicantCollege;
    }
}
