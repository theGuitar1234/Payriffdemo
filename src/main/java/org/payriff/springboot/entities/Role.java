package org.payriff.springboot.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ROLE")
public class Role {
    @Id
    private Long roleId;

    @Column
    private String roleNameString;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
       this.users.add(user);
    }
    public void removeUser(User user) {
        this.users.remove(user);
    }
}
