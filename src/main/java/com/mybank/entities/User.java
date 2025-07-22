package com.mybank.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mybank.dtos.utilities.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    @Column(name="user_name", length = 50)
    private String userName;
    @Column(name="contact_no", length = 15, unique = true)
    private String contactNo;
    @Column(name="email", length = 100,unique = true)
    private String email;
    @Column(name="password", length = 500)
    private String password;
    @Column(name="role", length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;  //CUSTOMER, ADMIN, MANAGER
    //    System generated date
    @Column(name="registration_date")
    private Date registrationDate;
    //      Will be calculated using service layer during password reset in every 90days cycle
    private Date passwordExpiryDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference  //// to avoid recursion in REST responses.
    private List<Accounts> accounts = new ArrayList<>();

    public String getUserName() {
        return userName;
    }
   // -----------------------------SECURITY LOGIC-----------------


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
