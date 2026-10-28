package br.com.fiap.pet_saude.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.pet_saude.model.Papel;
import br.com.fiap.pet_saude.model.EnumPapel;

@Repository
public interface PapelRepository extends JpaRepository<Papel, Long> {
    Optional<Papel> findByDescricao(EnumPapel descricao);
}
