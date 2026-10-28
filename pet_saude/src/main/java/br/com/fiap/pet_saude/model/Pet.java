package br.com.fiap.pet_saude.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_tutor")
    private Tutor tutor;

    @NotBlank(message = "O nome do pet é obrigatório.")
    @Size(min = 1, max = 50, message = "O nome do pet deve ter entre 1 e 50 caracteres.")
    private String nome;

    @NotNull(message = "Selecione a espécie.")
    @Enumerated(EnumType.STRING)
    private EnumEspecie especie;

    @NotBlank(message = "A raça é obrigatória (use \"SRD\" se não souber).")
    @Size(min = 2, max = 50, message = "A raça deve ter entre 2 e 50 caracteres.")
    private String raca;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @PastOrPresent(message = "A data de nascimento não pode ser futura.")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate dataNascimento;

    @NotNull(message = "O peso é obrigatório.")
    @DecimalMin(value = "0.1", message = "O peso deve ser de, no mínimo, 0,1 kg.")
    @DecimalMax(value = "200.0", message = "O peso não pode ultrapassar 200 kg.")
    private Double peso;

    private Boolean ativo = true;

    public Pet() {
    }

    public Pet(Long id, Tutor tutor, String nome, EnumEspecie especie, String raca,
                LocalDate dataNascimento, Double peso, Boolean ativo) {
        this.id = id;
        this.tutor = tutor;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.peso = peso;
        this.ativo = ativo;
    }

    /** Copia apenas os dados editáveis pelo formulário, preservando id e tutor. */
    public void atualizarDados(Pet pet) {
        this.nome = pet.getNome();
        this.especie = pet.getEspecie();
        this.raca = pet.getRaca();
        this.dataNascimento = pet.getDataNascimento();
        this.peso = pet.getPeso();
        this.ativo = Boolean.TRUE.equals(pet.getAtivo());
    }

    public boolean estaAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EnumEspecie getEspecie() {
        return especie;
    }

    public void setEspecie(EnumEspecie especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
