package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的用户领域数据，并提供对应属性访问。
 */
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String studentNo;
    private String college;
    private String major;
    private String grade;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private Integer experience;
    private Integer level;
    private LocalDateTime canceledAt;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 获取编号。
     *
     * @return 编号
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置编号。
     *
     * @param id 业务数据编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名。
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码。
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取昵称。
     *
     * @return 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称。
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取头像。
     *
     * @return 头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像。
     *
     * @param avatar 参数 `avatar`
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取`StudentNo`。
     *
     * @return `StudentNo`
     */
    public String getStudentNo() {
        return studentNo;
    }

    /**
     * 设置`StudentNo`。
     *
     * @param studentNo 参数 `studentNo`
     */
    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    /**
     * 获取学院。
     *
     * @return 学院
     */
    public String getCollege() {
        return college;
    }

    /**
     * 设置学院。
     *
     * @param college 参数 `college`
     */
    public void setCollege(String college) {
        this.college = college;
    }

    /**
     * 获取`Major`。
     *
     * @return `Major`
     */
    public String getMajor() {
        return major;
    }

    /**
     * 设置`Major`。
     *
     * @param major 参数 `major`
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * 获取年级。
     *
     * @return 年级
     */
    public String getGrade() {
        return grade;
    }

    /**
     * 设置年级。
     *
     * @param grade 参数 `grade`
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * 获取邮箱。
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱。
     *
     * @param email 电子邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取`Phone`。
     *
     * @return `Phone`
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置`Phone`。
     *
     * @param phone 参数 `phone`
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取`Role`。
     *
     * @return `Role`
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置`Role`。
     *
     * @param role 参数 `role`
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 获取状态。
     *
     * @return 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 业务状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取经验值。
     *
     * @return 经验值
     */
    public Integer getExperience() {
        return experience;
    }

    /**
     * 设置经验值。
     *
     * @param experience 参数 `experience`
     */
    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    /**
     * 获取等级。
     *
     * @return 等级
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置等级。
     *
     * @param level 参数 `level`
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取`CanceledAt`。
     *
     * @return `CanceledAt`
     */
    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    /**
     * 设置`CanceledAt`。
     *
     * @param canceledAt 参数 `canceledAt`
     */
    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    /**
     * 获取`CancelReason`。
     *
     * @return `CancelReason`
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 设置`CancelReason`。
     *
     * @param cancelReason 参数 `cancelReason`
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    /**
     * 获取`CreatedAt`。
     *
     * @return `CreatedAt`
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置`CreatedAt`。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取`UpdatedAt`。
     *
     * @return `UpdatedAt`
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置`UpdatedAt`。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
