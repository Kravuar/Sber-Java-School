package net.kravuar.json;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "account")
class Account {
    @Id
    private Long id;
    @Column private String username;
    @Column private String email;
    @Column private boolean emailVerified;
}
