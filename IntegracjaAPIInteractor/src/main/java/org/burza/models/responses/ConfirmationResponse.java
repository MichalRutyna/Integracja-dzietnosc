package org.burza.models.responses;

import java.util.UUID;

public class ConfirmationResponse {
    public String taskId;
    public String status;
    public String message;
    public int progress; // 0-100
    public ConfirmationResponse(String taskId, String status, String message, int progress) {
        this.taskId = taskId;
        this.status = status;
        this.message = message;
        this.progress = progress;
    }
}
