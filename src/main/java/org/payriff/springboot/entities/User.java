package org.payriff.springboot.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(
        name = "user_id_generator", 
        sequenceName = "user_id_sequence", 
        allocationSize = 50, 
        initialValue = 1947872546
    )
    private Long id;

    @Column
    private Instant nextDate;

    @Column
    private boolean billingEnabled;

    public void updateNextPaymentDate() {
        this.nextDate = LocalDate
            .ofInstant(nextDate == null ? Instant.now() : nextDate, ZoneId.of("Asia/Baku"))
            .plusMonths(1)
            .atStartOfDay(ZoneId.of("Asia/Baku")).toInstant();
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "salary_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "salary_currency"))
    })
    private Money payment;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USERS_ROLES", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.addUser(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.removeUser(this);
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setUser(this);
    }
}
