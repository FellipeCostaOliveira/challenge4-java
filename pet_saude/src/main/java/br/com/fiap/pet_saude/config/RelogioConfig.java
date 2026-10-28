package br.com.fiap.pet_saude.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Relógio único da aplicação, no fuso de São Paulo.
 * <p>
 * O servidor de deploy roda em UTC; usando este {@link Clock} o "hoje" e o
 * "agora" das regras de agenda seguem o horário da clínica. Também permite
 * fixar a data nos testes unitários.
 */
@Configuration
public class RelogioConfig {

    public static final ZoneId FUSO_CLINICA = ZoneId.of("America/Sao_Paulo");

    @Bean
    public Clock relogio() {
        return Clock.system(FUSO_CLINICA);
    }
}
