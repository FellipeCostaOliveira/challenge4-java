package br.com.fiap.pet_saude.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.fiap.pet_saude.dto.CadastroTutorForm;
import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import br.com.fiap.pet_saude.model.EnumPapel;
import br.com.fiap.pet_saude.model.Papel;
import br.com.fiap.pet_saude.model.Tutor;
import br.com.fiap.pet_saude.model.Usuario;
import br.com.fiap.pet_saude.repository.PapelRepository;
import br.com.fiap.pet_saude.repository.TutorRepository;
import br.com.fiap.pet_saude.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CadastroServiceTest {

    @Mock
    private TutorRepository repT;

    @Mock
    private UsuarioRepository repU;

    @Mock
    private PapelRepository repPapel;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private CadastroService cadastroService;

    private CadastroTutorForm form;

    @BeforeEach
    void setUp() {
        form = new CadastroTutorForm();
        form.setNome("Carla Souza");
        form.setCpf("321.654.987-91");
        form.setEmail("Carla@Email.com");
        form.setTelefone("(11) 95555-4444");
        form.setUsername("carla");
        form.setSenha("segredo1");
        form.setConfirmarSenha("segredo1");
    }

    @Test
    void deveCadastrarTutorComSenhaCriptografadaEDadosNormalizados() {
        when(repU.existsByUsername("carla")).thenReturn(false);
        when(repT.existsByCpf("32165498791")).thenReturn(false);
        when(repT.existsByEmail("carla@email.com")).thenReturn(false);
        when(repT.save(any(Tutor.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(repPapel.findByDescricao(EnumPapel.TUTOR)).thenReturn(Optional.of(new Papel(2L, EnumPapel.TUTOR)));
        when(encoder.encode("segredo1")).thenReturn("hash");
        when(repU.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario usuario = cadastroService.cadastrarTutor(form);

        assertEquals("hash", usuario.getSenha());
        assertEquals("32165498791", usuario.getTutor().getCpf());
        assertEquals("11955554444", usuario.getTutor().getTelefone());
        assertEquals("carla@email.com", usuario.getTutor().getEmail());
        assertEquals(1, usuario.getPapeis().size());
    }

    @Test
    void deveRecusarSenhasDiferentes() {
        form.setConfirmarSenha("outra");

        assertThrows(RegraDeNegocioException.class, () -> cadastroService.cadastrarTutor(form));
        verify(repT, never()).save(any());
    }

    @Test
    void deveRecusarUsuarioJaExistente() {
        when(repU.existsByUsername("carla")).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> cadastroService.cadastrarTutor(form));
        verify(repT, never()).save(any());
    }

    @Test
    void deveRecusarCpfJaCadastrado() {
        when(repU.existsByUsername("carla")).thenReturn(false);
        when(repT.existsByCpf("32165498791")).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> cadastroService.cadastrarTutor(form));
    }
}
