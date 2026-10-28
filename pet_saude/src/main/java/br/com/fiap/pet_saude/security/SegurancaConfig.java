package br.com.fiap.pet_saude.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import br.com.fiap.pet_saude.model.EnumPapel;

@Configuration
@EnableWebSecurity
public class SegurancaConfig {

    private static final String[] ROTAS_PUBLICAS = {
            "/login", "/cadastro", "/acesso_negado", "/error",
            "/css/**", "/js/**", "/img/**", "/favicon.ico", "/h2-console/**"
    };

    private static final String[] ROTAS_DA_CLINICA = { "/tutores/**", "/admin/**" };

    @Bean
    public SecurityFilterChain filtrar(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req -> req
                        .requestMatchers(ROTAS_PUBLICAS).permitAll()
                        .requestMatchers(ROTAS_DA_CLINICA).hasAuthority(EnumPapel.ADMIN.name())
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .formLogin(login -> login.loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?falha=true")
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())
                .exceptionHandling(excecao -> excecao.accessDeniedHandler(
                        (request, response, acessoNegado) -> response.sendRedirect("/acesso_negado")));

        return http.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
