package com.hr.freerein.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Small request bodies used by the new endpoints (keeps entities safe from mass-assignment). */
public final class Requests {
    private Requests() {}

    public record CaseRequest(
            @NotBlank @Size(max = 200) String title,
            String description,
            String notes) {}

    public record StatusRequest(@NotBlank String status) {}

    public record NoteRequest(@NotBlank String note) {}

    /** List-like fields (safePlaces, emergencyContacts, ...) are JSON text produced by the React form. */
    public record SafetyPlanRequest(
            @NotBlank @Size(max = 200) String title,
            String steps,
            String emergencyContacts,
            String notes,
            String safePlaces,
            String importantDocuments,
            String safetySteps,
            String supportServices,
            String personalNotes) {}

    public record SupportRequestCreate(
            @NotBlank @Size(max = 200) String subject,
            String description,
            String requestType,
            String urgency,
            Boolean anonymous) {}

    /** Partial update: any field left null is not changed. */
    public record SupportUpdate(String status, String assignedTo, String notes) {}

    public record MessageRequest(@NotBlank String content, Boolean internal) {}

    public record ProfileRequest(
            @Size(min = 2, max = 100) String name,
            @Size(max = 30) String phone,
            String bio,
            @Size(max = 150) String specialization,
            Boolean available) {}

    public record EvidenceRequest(
            @NotBlank @Size(max = 200) String title,
            String description,
            String dateOfIncident,
            String fileName,
            String fileType) {}

    public record ChatSend(@NotBlank String content, @NotBlank String receiverEmail) {}

    public record ActiveRequest(boolean active) {}

    public record RoleRequest(@NotBlank String role) {}
}
