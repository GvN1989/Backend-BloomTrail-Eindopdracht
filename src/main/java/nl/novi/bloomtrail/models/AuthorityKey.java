package nl.novi.bloomtrail.models;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AuthorityKey implements Serializable {
    private String username;
    private String authority;
}