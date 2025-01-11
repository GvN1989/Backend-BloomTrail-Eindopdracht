package nl.novi.bloomtrail.models;

import jakarta.persistence.*;


@Entity
@Table(name = "uploads")

public class Upload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String url;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = true)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "strength_results_id", nullable = true)
    private StrengthResults strengthResults;

    @ManyToOne
    @JoinColumn(name = "sessionInsights_id", nullable = true)
    private SessionInsights sessionInsights;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public StrengthResults getStrengthResults() {
        return strengthResults;
    }

    public void setStrengthResults(StrengthResults strengthResults) {
        this.strengthResults = strengthResults;
    }

    public SessionInsights getSessionInsights() {
        return sessionInsights;
    }

    public void setSessionInsights(SessionInsights sessionInsights) {
        this.sessionInsights = sessionInsights;
    }
}
