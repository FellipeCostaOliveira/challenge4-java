package br.com.fiap.pet_saude.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.pet_saude.model.Tutor;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    List<Tutor> findAllByOrderByNomeAsc();

    List<Tutor> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
