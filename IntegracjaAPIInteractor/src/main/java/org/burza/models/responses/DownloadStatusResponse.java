package org.burza.models.responses;

public class DownloadStatusResponse {
    public String status;
    public int progress;

    public DownloadStatusResponse(String status, int progress) {
        this.status = status;
        this.progress = progress;
    }
}
//}
