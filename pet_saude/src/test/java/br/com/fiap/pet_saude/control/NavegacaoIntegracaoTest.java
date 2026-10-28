package br.com.fiap.pet_saude.control;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Percorre os principais fluxos com a aplicação completa (H2 + Flyway + Security + Thymeleaf).
 * Além de validar as regras de acesso, garante que todas as telas renderizam sem erro.
 * IDs vêm das migrations V4/V5: tutor 1 = Maria (pets 1 Rex e 2 Mia), tutor 2 = João (pet 3 Thor).
 */
@SpringBootTest
@Transactional
class NavegacaoIntegracaoTest {

    @Autowired
    private WebApplicationContext contexto;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void visitanteVeLoginECadastroMasNaoAHome() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/cadastro")).andExpect(status().isOk());
        mvc.perform(get("/home")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
    }

    @Test
    void visitanteConsegueSeCadastrarComoTutor() throws Exception {
        mvc.perform(post("/cadastro").with(csrf())
                        .param("nome", "Carla Souza")
                        .param("cpf", "32165498791")
                        .param("email", "carla@petpath.com")
                        .param("telefone", "11955554444")
                        .param("username", "carlasouza")
                        .param("senha", "segredo1")
                        .param("confirmarSenha", "segredo1"))
                .andExpect(redirectedUrl("/login?cadastroRealizado=true"));
    }

    @Test
    void cadastroInvalidoVoltaParaOFormulario() throws Exception {
        mvc.perform(post("/cadastro").with(csrf())
                        .param("nome", "C")
                        .param("cpf", "11111111111")
                        .param("senha", "1")
                        .param("confirmarSenha", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void equipeDaClinicaNavegaPorTodasAsTelas() throws Exception {
        for (String url : new String[] { "/home", "/pets", "/pets?busca=rex", "/pets/1", "/pets/3",
                "/consultas", "/consultas?status=REALIZADA", "/consultas/agendar/1", "/tutores",
                "/tutores?busca=maria", "/tutores/1", "/pets/1/editar" }) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
    }

    @Test
    @WithUserDetails("mariasilva")
    void tutoraNavegaPelasPropriasTelas() throws Exception {
        for (String url : new String[] { "/home", "/pets", "/pets/novo", "/pets/1", "/pets/1/editar",
                "/consultas", "/consultas/agendar/2" }) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
    }

    @Test
    @WithUserDetails("mariasilva")
    void tutoraNaoAcessaAreaDaClinicaNemPetDeOutroTutor() throws Exception {
        mvc.perform(get("/tutores")).andExpect(redirectedUrl("/acesso_negado"));
        mvc.perform(get("/pets/3")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("mariasilva")
    void tutoraCadastraPetPeloFormulario() throws Exception {
        mvc.perform(post("/pets").with(csrf())
                        .param("nome", "Bolinha")
                        .param("especie", "GATO")
                        .param("raca", "SRD")
                        .param("dataNascimento", "2024-01-10")
                        .param("peso", "3.5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/pets/*"));
    }

    @Test
    @WithUserDetails("admin")
    void urlInexistenteMostraPaginaDeErroAmigavel() throws Exception {
        mvc.perform(get("/pets/9999")).andExpect(status().isNotFound());
        mvc.perform(get("/consultas?status=INVALIDO")).andExpect(status().isBadRequest());
    }
}
