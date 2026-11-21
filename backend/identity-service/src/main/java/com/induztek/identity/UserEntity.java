package com.induztek.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "usuarios")
public class UserEntity {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 40)
    private String roleCode;

    @Column(length = 120)
    private String company;

    @Column(nullable = false)
    private boolean termsAccepted;

    @Column(nullable = false)
    private Instant termsAcceptedAt;

    @Column(nullable = false)
    private boolean active;

    @Column(length = 2048)
    private String fotoPerfil;

    @Column(length = 2048)
    private String photoUrl;

    @Column(length = 500)
    private String biography;

    protected UserEntity() {
    }

    public UserEntity(String id, String email, String passwordHash, String name, String roleCode,
            String company, Instant termsAcceptedAt, boolean active) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.roleCode = roleCode;
        this.company = company;
        this.termsAccepted = true;
        this.termsAcceptedAt = termsAcceptedAt;
        this.active = active;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getRoleCode() { return roleCode; }
    public String getCompany() { return company; }
    public boolean isTermsAccepted() { return termsAccepted; }
    public Instant getTermsAcceptedAt() { return termsAcceptedAt; }
    public boolean isActive() { return active; }
    public String getFotoPerfil() { return fotoPerfil != null ? fotoPerfil : photoUrl; }
    public String getPhotoUrl() { return photoUrl != null ? photoUrl : fotoPerfil; }
    public String getBiography() { return biography; }
    public void setActive(boolean active) { this.active = active; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; this.photoUrl = fotoPerfil; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; this.fotoPerfil = photoUrl; }
    public void setBiography(String biography) { this.biography = biography; }
}
