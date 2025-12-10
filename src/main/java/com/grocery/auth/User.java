package com.grocery.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="users")
@Data
@Setter
@Getter
public class User implements UserDetails {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
    @JsonIgnore
	private String passwordHash;
	private String fullName;
	private String mobile;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	private Set<Role> roles = new HashSet<>();
	@Column
	private String addressLine;
	@Column
	private String city;
	@Column
	private String pinCode;
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus = AccountStatus.ACTIVE;

    // ------------------ UserDetails Implementation --------------------
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✅ Map your existing Set<Role> to a Collection of GrantedAuthority
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
    @Override
    @JsonIgnore
    public String getPassword() {
        return this.passwordHash;
    }
    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email; // Use email as the username
    }
    // Always return true unless you implement specific account expiration/locking features
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() { return true; }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() { return true; }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    @JsonIgnore
    public boolean isEnabled() { return this.accountStatus == AccountStatus.ACTIVE; }
}
