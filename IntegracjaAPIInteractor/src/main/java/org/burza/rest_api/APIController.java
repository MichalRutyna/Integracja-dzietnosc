package org.burza.rest_api;


import org.burza.models.responses.ConfirmationResponse;
import org.burza.models.responses.DownloadStatusResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class APIController {

    private final DownloadService taskService;

    public APIController(DownloadService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/download")
    public ResponseEntity<EntityModel<ConfirmationResponse>> download(@RequestParam String dataset) {
        UUID taskId = taskService.startTask(dataset);

        if (taskId == null) {
            return ResponseEntity.notFound().build();
        }

        ConfirmationResponse response = new ConfirmationResponse(taskId.toString(), "accepted", "Task has been started", 0);

        EntityModel<ConfirmationResponse> resource = EntityModel.of(response);
        resource.add(linkTo(methodOn(APIController.class).getStatus(taskId)).withRel("status"));

        return ResponseEntity.accepted().body(resource); // HTTP 202
    }

    @GetMapping("/status")
    public ResponseEntity<DownloadStatusResponse> getStatus(@RequestParam UUID taskId) {
        try {
            return ResponseEntity.ok(taskService.getProgress(taskId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/datasets")
    public ResponseEntity<Set<String>> getAvailableDatasets() {
        return ResponseEntity.ok(taskService.getAllowedDatasets());
    }
}