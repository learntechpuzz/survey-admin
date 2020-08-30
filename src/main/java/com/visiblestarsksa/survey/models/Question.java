package com.visiblestarsksa.survey.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
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

    private Long survey_id;

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

    @OneToMany(mappedBy = "question_id", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers;
}