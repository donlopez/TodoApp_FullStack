package todo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "todo_items")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TodoItem() {}

    public TodoItem(String description) {
        this.description = description;
    }

    /** Ensure createdAt is set whenever the row is first persisted */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean c) { this.completed = c; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** Helper for JSP (JSTL’s fmt tag expects java.util.Date) */
    public String getCreatedAtFormatted() {
        return createdAt == null ? "" :
                createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "[" + id + "] " + description +
                (completed ? " (done)" : "") +
                " - Created at: " + createdAt;
    }
}
