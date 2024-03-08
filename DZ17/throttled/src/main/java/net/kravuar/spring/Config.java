package net.kravuar.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Config {
    @Bean
    TimeLimitRebalancer rebalancer() {
        return new SimpleRebalancer();
    }

    @Bean
    FilesDownloader filesDownloader(TimeLimitRebalancer rebalancer) {
        return new FilesDownloader(rebalancer);
    }
}
