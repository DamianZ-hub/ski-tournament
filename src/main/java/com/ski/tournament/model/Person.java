package com.ski.tournament.model;

import com.ski.tournament.config.UserRole;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Person extends AbstractEntity {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String gender;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    private String password;

    private String phone;

    private String activationCode;

    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> authorities;

    public Set<UserRole> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<UserRole> authorities) {
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(@NotNull String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotNull LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(@NotNull Unit unit) {
        this.unit = unit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Person(@NotNull String firstName, @NotNull String lastName, @NotNull String gender, @NotNull LocalDate dateOfBirth, @NotNull Unit unit, @NotNull String username, @NotNull String password, @NotNull Collection<UserRole> authorities, String phone, boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.unit = unit;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
        this.enabled = enabled;
    }

    public Person(@NotNull String firstName, @NotNull String lastName, @NotNull String gender, @NotNull LocalDate dateOfBirth, @NotNull Unit unit, @NotNull String username, @NotNull String password, @NotNull Collection<UserRole> authorities, @NotNull boolean enabled) {
       this(firstName, lastName, gender, dateOfBirth, unit,username, password, authorities, null, enabled);
    }

    public void generateActivationCode(){
        this.setActivationCode(RandomStringUtils.randomAlphanumeric(32));
    }

    private static SortedSet<UserRole> sortAuthorities(Collection<UserRole> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<UserRole> sortedAuthorities = new TreeSet(new Person.AuthorityComparator());
        Iterator var2 = authorities.iterator();

        while(var2.hasNext()) {
            UserRole grantedAuthority = (UserRole)var2.next();
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    public Person() {
        authorities = new TreeSet<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        return Objects.equals(getId(), other.getId());
    }

    public String getFirstNameLastNameAndUnit(){
        return getFirstName() + " " + getLastName() + " - " + getUnit().getShortName();
    }

    public void setAuthorities(Set<UserRole> authorities) {
        this.authorities = authorities;
    }

    private static class AuthorityComparator implements Comparator<UserRole>, Serializable {
        private static final long serialVersionUID = 540L;

        private AuthorityComparator() {
        }

        public int compare(UserRole g1, UserRole g2) {
            if (g2.name() == null) {
                return -1;
            } else {
                return g1.name() == null ? 1 : g1.name().compareTo(g2.name());
            }
        }
    }
}
