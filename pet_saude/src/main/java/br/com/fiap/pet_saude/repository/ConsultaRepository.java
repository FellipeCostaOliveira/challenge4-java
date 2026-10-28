package br.com.fiap.pet_saude.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.fiap.pet_saude.model.Consulta;
import br.com.fiap.pet_saude.model.EnumStatusConsulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    long countByStatus(EnumStatusConsulta status);

    List<Consulta> findAllByOrderByDataConsultaDescHorarioDesc();

    List<Consulta> findByPetIdOrderByDataConsultaDescHorarioDesc(Long petId);

    List<Consulta> findByDataConsultaAndStatusOrderByHorarioAsc(LocalDate dataConsulta, EnumStatusConsulta status);

    @Query("select c from Consulta c where c.pet.tutor.id = :tutorId order by c.dataConsulta desc, c.horario desc")
    List<Consulta> buscarPorTutor(@Param("tutorId") Long tutorId);

    boolean existsByPetId(Long petId);

    boolean existsByPetIdAndDataConsultaAndHorarioAndStatus(
            Long petId, LocalDate dataConsulta, LocalTime horario, EnumStatusConsulta status);
}
