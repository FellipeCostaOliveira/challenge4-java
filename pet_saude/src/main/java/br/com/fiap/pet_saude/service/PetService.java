package br.com.fiap.pet_saude.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.pet_saude.exception.AcessoNegadoException;
import br.com.fiap.pet_saude.exception.RegistroNaoEncontradoException;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.model.Tutor;
import br.com.fiap.pet_saude.model.Usuario;
import br.com.fiap.pet_saude.repository.ConsultaRepository;
import br.com.fiap.pet_saude.repository.PetRepository;
import br.com.fiap.pet_saude.security.UsuarioLogadoService;

@Service
public class PetService {

    /** Resultado da remoção: pets com histórico são inativados para preservar o prontuário. */
    public enum ResultadoRemocao { EXCLUIDO, INATIVADO }

    private final PetRepository repP;
    private final ConsultaRepository repC;
    private final UsuarioLogadoService usuarioLogadoService;

    public PetService(PetRepository repP, ConsultaRepository repC, UsuarioLogadoService usuarioLogadoService) {
        this.repP = repP;
        this.repC = repC;
        this.usuarioLogadoService = usuarioLogadoService;
    }

    public List<Pet> listarVisiveis(String busca) {
        List<Pet> pets = usuarioLogadoService.isAdmin()
                ? repP.findAllByOrderByNomeAsc()
                : repP.findByTutorIdOrderByNomeAsc(usuarioLogadoService.obterTutorLogado().getId());
        if (busca == null || busca.isBlank()) {
            return pets;
        }
        String termo = busca.trim().toLowerCase();
        return pets.stream()
                .filter(p -> p.getNome().toLowerCase().contains(termo)
                        || p.getRaca().toLowerCase().contains(termo)
                        || p.getTutor().getNome().toLowerCase().contains(termo))
                .toList();
    }

    public Pet buscarComPermissao(Long id) {
        Pet pet = repP.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Pet não encontrado."));
        garantirPermissao(pet);
        return pet;
    }

    /** Apenas tutores cadastram pets (o pet nasce vinculado ao tutor logado). */
    public void garantirQuePodeCadastrar() {
        usuarioLogadoService.obterTutorLogado();
    }

    @Transactional
    public Pet cadastrar(Pet pet) {
        Tutor tutor = usuarioLogadoService.obterTutorLogado();
        pet.setId(null);
        pet.setTutor(tutor);
        pet.setAtivo(true);
        return repP.save(pet);
    }

    @Transactional
    public Pet atualizar(Long id, Pet dadosNovos) {
        Pet pet = buscarComPermissao(id);
        pet.atualizarDados(dadosNovos);
        return repP.save(pet);
    }

    @Transactional
    public ResultadoRemocao remover(Long id) {
        Pet pet = buscarComPermissao(id);
        if (repC.existsByPetId(id)) {
            pet.setAtivo(false);
            repP.save(pet);
            return ResultadoRemocao.INATIVADO;
        }
        repP.delete(pet);
        return ResultadoRemocao.EXCLUIDO;
    }

    private void garantirPermissao(Pet pet) {
        if (usuarioLogadoService.isAdmin()) {
            return;
        }
        Usuario logado = usuarioLogadoService.obterUsuarioLogado();
        boolean donoDoPet = logado.getTutor() != null
                && pet.getTutor() != null
                && pet.getTutor().getId().equals(logado.getTutor().getId());
        if (!donoDoPet) {
            throw new AcessoNegadoException("Este pet não pertence ao tutor logado.");
        }
    }
}
