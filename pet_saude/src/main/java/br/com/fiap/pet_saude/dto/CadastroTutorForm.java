package br.com.fiap.pet_saude.dto;

import org.hibernate.validator.constraints.br.CPF;

import br.com.fiap.pet_saude.model.Tutor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados do formulário público de cadastro (/cadastro).
 * Separa o que vem da tela (inclusive usuário e senha) da entidade {@link Tutor},
 * evitando expor a entidade JPA diretamente ao binding da requisição.
 */
public class CadastroTutorForm {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "O CPF informado é inválido.")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 80, message = "O e-mail deve ter, no máximo, 80 caracteres.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Informe o telefone com DDD (10 a 15 dígitos).")
    private String telefone;

    @NotBlank(message = "Escolha um nome de usuário.")
    @Size(min = 4, max = 30, message = "O usuário deve ter entre 4 e 30 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Use apenas letras, números, ponto, hífen ou sublinhado.")
    private String username;

    @NotBlank(message = "Crie uma senha.")
    @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres.")
    private String senha;

    @NotBlank(message = "Confirme a senha.")
    private String confirmarSenha;

    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
    }

    public Tutor paraTutor() {
        return new Tutor(null, nome.trim(), somenteDigitos(cpf), email.trim().toLowerCase(), somenteDigitos(telefone));
    }

    public String getCpfNormalizado() {
        return somenteDigitos(cpf);
    }

    public String getEmailNormalizado() {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}
