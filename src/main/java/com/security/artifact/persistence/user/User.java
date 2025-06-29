package com.security.artifact.persistence.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.security.artifact.token.Token;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User implements UserDetails{
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String firstName;
	
	private String lastName;
	
	private String secondLastName;
	
	@Column(unique = true)
	private String email;
	
	private String password;

	private String provider;
	
	private int userWeight;
	
	private int userHeight;
	
	@CreationTimestamp
	@Column(name = "registration_date", nullable = false, updatable = false)
	private Date registrationDate;
	
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;
	
	@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE}, orphanRemoval=true)
	private List<Token> tokens = new ArrayList<>();
	
	/*@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE}, orphanRemoval=true)
	private List<Exercise> exercises = new ArrayList<>();
	
	@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE}, orphanRemoval=true)
	private List<Routine> routines = new ArrayList<>();
	
	@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE}, orphanRemoval=true)
	private List<Workout> workouts = new ArrayList<>();
	
	@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE}, orphanRemoval=true)
	private List<FavouriteExercise> favouriteExercises = new ArrayList<>();*/
	
	public User(int id) {
		this.id = id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	
	
}
