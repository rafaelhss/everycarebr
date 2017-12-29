package br.com.everycare.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Professional.
 */
@Entity
@Table(name = "professional")
public class Professional implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "telegram_id", nullable = false)
    private String telegramId;

    @OneToMany(mappedBy = "professional")
    @JsonIgnore
    private Set<Answer> answers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Professional name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public Professional telegramId(String telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public Professional answers(Set<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public Professional addAnswer(Answer answer) {
        this.answers.add(answer);
        answer.setProfessional(this);
        return this;
    }

    public Professional removeAnswer(Answer answer) {
        this.answers.remove(answer);
        answer.setProfessional(null);
        return this;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Professional professional = (Professional) o;
        if (professional.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), professional.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Professional{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", telegramId='" + getTelegramId() + "'" +
            "}";
    }
}
