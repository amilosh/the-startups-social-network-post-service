package faang.school.postservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_short_info")
public class UserShortInfo {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "small_file_id")
    private String smallFileId;

    @Column(name = "saved_date_time", nullable = false, updatable = false, insertable = false)
    private LocalDateTime savedDateTime;

    public UserShortInfo(Long userId, String username, String fileId, String smallFileId) {
        this.userId = userId;
        this.username = username;
        this.fileId = fileId;
        this.smallFileId = smallFileId;
    }
}

