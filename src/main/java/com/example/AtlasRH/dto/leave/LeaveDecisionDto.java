package com.example.AtlasRH.dto.leave;

import io.swagger.v3.oas.annotations.media.Schema;

public class LeaveDecisionDto {

    @Schema(description = "Commentaire du manager (obligatoire en cas de refus, facultatif en cas d'approbation)", example = "Approuvé, bon repos.")
    private String comment;

    public LeaveDecisionDto() {
    }

    public LeaveDecisionDto(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
