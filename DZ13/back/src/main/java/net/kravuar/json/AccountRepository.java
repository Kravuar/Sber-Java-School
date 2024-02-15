package net.kravuar.json;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AccountRepository extends R2dbcRepository<Account, Long> {
}