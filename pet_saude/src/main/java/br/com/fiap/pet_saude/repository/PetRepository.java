package br.com.fiap.pet_saude.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.pet_saude.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findAllByOrderByNomeAsc();

    List<Pet> findByTutorIdOrderByNomeAsc(Long tutorId);

    List<Pet> findByAtivoTrueOrderByNomeAsc();

    long countByAtivoTrue();
}
