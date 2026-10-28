package br.com.fiap.pet_saude.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "consulta")
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_pet")
    private Pet pet;

    @NotNull(message = "A data da consulta é obrigatória.")
    @FutureOrPresent(message = "A data da consulta não pode estar no passado.")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate dataConsulta;

    @NotNull(message = "O horário da consulta é obrigatório.")
    @DateTimeFormat(iso = ISO.TIME)
    private LocalTime horario;

    @NotNull(message = "O tipo de consulta é obrigatório.")
    @Enumerated(EnumType.STRING)
    private EnumTipoConsulta tipo;

    @NotBlank(message = "Descreva o motivo da consulta.")
    @Size(min = 5, max = 200, message = "O motivo deve ter entre 5 e 200 caracteres.")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private EnumStatusConsulta status = EnumStatusConsulta.AGENDADA;

    @Size(max = 300, message = "As observações devem ter, no máximo, 300 caracteres.")
    private String observacoes;

    public Consulta() {
    }

    public Consulta(Long id, Pet pet, LocalDate dataConsulta, LocalTime horario,
                     EnumTipoConsulta tipo, String descricao, EnumStatusConsulta status) {
        this.id = id;
        this.pet = pet;
        this.dataConsulta = dataConsulta;
        this.horario = horario;
        this.tipo = tipo;
        this.descricao = descricao;
        this.status = status;
    }

    public boolean isAgendada() {
        return status == EnumStatusConsulta.AGENDADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDate dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public EnumTipoConsulta getTipo() {
        return tipo;
    }

    public void setTipo(EnumTipoConsulta tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EnumStatusConsulta getStatus() {
        return status;
    }

    public void setStatus(EnumStatusConsulta status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
