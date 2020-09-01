package com.visiblestarsksa.survey.models;

import static com.visiblestarsksa.survey.util.SurveyConfig.*;

import com.visiblestarsksa.survey.util.CryptoUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.Table;

@Slf4j
@Entity
@Table(name = "survey_users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SurveyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long survey_id;

    private Date extraction_date;

    private Long branch_code;

    private Long mask_party;

    private String served_by;

    private String trans_desc;

    private String segment;

    private String gender;

    private Timestamp survey_expiry;

    private String survey_url;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "survey_user_id")
    private Set<UserResponse> responses;

    @PostPersist
    public void generateSurveyData() {
        this.survey_url = SURVEY_URL_PREFIX + "?st=" + CryptoUtil.encrypt(String.valueOf(id));
        log.debug("survey_url: {}", survey_url);
    }
}
