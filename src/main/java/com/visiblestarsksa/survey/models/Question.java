package com.visiblestarsksa.survey.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Embeddable
@Table(name = "survey_questions")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer step_no;

    @NotBlank
    @Size(max = 255)
    private String question_en;

    @NotBlank
    @Size(max = 255)
    private String question_ar;

    private boolean required;

    @Enumerated(EnumType.STRING)
    private EQuestionType type;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "survey_answers", joinColumns = @JoinColumn(name = "question_id"))
    private Set<Answer> answers;
}
