package br.com.fiap.pet_saude.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.pet_saude.exception.RegistroNaoEncontradoException;
import br.com.fiap.pet_saude.model.Pet;
import br.com.fiap.pet_saude.model.Tutor;
import br.com.fiap.pet_saude.repository.PetRepository;
import br.com.fiap.pet_saude.repository.TutorRepository;

/** Consultas de tutores usadas pela equipe da clínica (rotas /tutores, restritas a ADMIN). */
@Service
public class TutorService {

    private final TutorRepository repT;
    private final PetRepository repP;

    public TutorService(TutorRepository repT, PetRepository repP) {
        this.repT = repT;
        this.repP = repP;
    }

    public List<Tutor> listar(String busca) {
        if (busca == null || busca.isBlank()) {
            return repT.findAllByOrderByNomeAsc();
        }
        return repT.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim());
    }

    public Tutor buscar(Long id) {
        return repT.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Tutor não encontrado."));
    }

    public List<Pet> listarPets(Long tutorId) {
        return repP.findByTutorIdOrderByNomeAsc(tutorId);
    }
}
