package net.kravuar;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountInfoWithBalance {
    private final long id;
    private double balance;
}
