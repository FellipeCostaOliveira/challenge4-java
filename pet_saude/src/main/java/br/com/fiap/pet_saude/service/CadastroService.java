package br.com.fiap.pet_saude.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.pet_saude.dto.CadastroTutorForm;
import br.com.fiap.pet_saude.exception.RegraDeNegocioException;
import br.com.fiap.pet_saude.model.EnumPapel;
import br.com.fiap.pet_saude.model.Papel;
import br.com.fiap.pet_saude.model.Tutor;
import br.com.fiap.pet_saude.model.Usuario;
import br.com.fiap.pet_saude.repository.PapelRepository;
import br.com.fiap.pet_saude.repository.TutorRepository;
import br.com.fiap.pet_saude.repository.UsuarioRepository;

/** Autocadastro do tutor: cria o Tutor e o Usuario (papel TUTOR) na mesma transação. */
@Service
public class CadastroService {

    private final TutorRepository repT;
    private final UsuarioRepository repU;
    private final PapelRepository repPapel;
    private final PasswordEncoder encoder;

    public CadastroService(TutorRepository repT, UsuarioRepository repU,
                           PapelRepository repPapel, PasswordEncoder encoder) {
        this.repT = repT;
        this.repU = repU;
        this.repPapel = repPapel;
        this.encoder = encoder;
    }

    @Transactional
    public Usuario cadastrarTutor(CadastroTutorForm form) {
        validarDadosUnicos(form);

        Tutor tutor = repT.save(form.paraTutor());

        Papel papelTutor = repPapel.findByDescricao(EnumPapel.TUTOR)
                .orElseThrow(() -> new IllegalStateException(
                        "Papel TUTOR não encontrado. Verifique se as migrations do Flyway rodaram."));

        Usuario usuario = new Usuario();
        usuario.setUsername(form.getUsername().trim());
        usuario.setSenha(encoder.encode(form.getSenha()));
        usuario.setNomePerfil(tutor.getNome());
        usuario.setTutor(tutor);
        usuario.setPapeis(Set.of(papelTutor));
        return repU.save(usuario);
    }

    private void validarDadosUnicos(CadastroTutorForm form) {
        if (!form.senhasConferem()) {
            throw new RegraDeNegocioException("As senhas informadas não conferem.");
        }
        if (repU.existsByUsername(form.getUsername().trim())) {
            throw new RegraDeNegocioException("Esse nome de usuário já está em uso. Escolha outro.");
        }
        if (repT.existsByCpf(form.getCpfNormalizado())) {
            throw new RegraDeNegocioException("Já existe um tutor cadastrado com esse CPF.");
        }
        if (repT.existsByEmail(form.getEmailNormalizado())) {
            throw new RegraDeNegocioException("Já existe um tutor cadastrado com esse e-mail.");
        }
    }
}
