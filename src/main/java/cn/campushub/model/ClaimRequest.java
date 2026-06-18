package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的认领申请领域数据，并提供对应属性访问。
 */
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

    /**
     * 获取编号。
     *
     * @return 编号
     */
    public Long getId() { return id; }
    /**
     * 设置编号。
     *
     * @param id 业务数据编号
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取`LostFoundId`。
     *
     * @return `LostFoundId`
     */
    public Long getLostFoundId() { return lostFoundId; }
    /**
     * 设置`LostFoundId`。
     *
     * @param lostFoundId `lostFound`编号
     */
    public void setLostFoundId(Long lostFoundId) { this.lostFoundId = lostFoundId; }
    /**
     * 获取用户编号。
     *
     * @return 用户编号
     */
    public Long getUserId() { return userId; }
    /**
     * 设置用户编号。
     *
     * @param userId 用户编号
     */
    public void setUserId(Long userId) { this.userId = userId; }
    /**
     * 获取消息。
     *
     * @return 消息
     */
    public String getMessage() { return message; }
    /**
     * 设置消息。
     *
     * @param message 消息数据
     */
    public void setMessage(String message) { this.message = message; }
    /**
     * 获取`Contact`。
     *
     * @return `Contact`
     */
    public String getContact() { return contact; }
    /**
     * 设置`Contact`。
     *
     * @param contact 参数 `contact`
     */
    public void setContact(String contact) { this.contact = contact; }
    /**
     * 获取状态。
     *
     * @return 状态
     */
    public String getStatus() { return status; }
    /**
     * 设置状态。
     *
     * @param status 业务状态
     */
    public void setStatus(String status) { this.status = status; }
    /**
     * 获取`CreatedAt`。
     *
     * @return `CreatedAt`
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置`CreatedAt`。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /**
     * 获取`HandledAt`。
     *
     * @return `HandledAt`
     */
    public LocalDateTime getHandledAt() { return handledAt; }
    /**
     * 设置`HandledAt`。
     *
     * @param handledAt 参数 `handledAt`
     */
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
    /**
     * 获取`ApplicantNickname`。
     *
     * @return `ApplicantNickname`
     */
    public String getApplicantNickname() { return applicantNickname; }
    /**
     * 设置`ApplicantNickname`。
     *
     * @param applicantNickname 参数 `applicantNickname`
     */
    public void setApplicantNickname(String applicantNickname) {
        this.applicantNickname = applicantNickname;
    }
    /**
     * 获取`ApplicantAvatar`。
     *
     * @return `ApplicantAvatar`
     */
    public String getApplicantAvatar() { return applicantAvatar; }
    /**
     * 设置`ApplicantAvatar`。
     *
     * @param applicantAvatar 参数 `applicantAvatar`
     */
    public void setApplicantAvatar(String applicantAvatar) {
        this.applicantAvatar = applicantAvatar;
    }
    /**
     * 获取`ApplicantCollege`。
     *
     * @return `ApplicantCollege`
     */
    public String getApplicantCollege() { return applicantCollege; }
    /**
     * 设置`ApplicantCollege`。
     *
     * @param applicantCollege 参数 `applicantCollege`
     */
    public void setApplicantCollege(String applicantCollege) {
        this.applicantCollege = applicantCollege;
    }
}
