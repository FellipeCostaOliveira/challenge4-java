# 🐾 PetPath Saúde — Challenge 4 (Java Advanced)
 
> **FIAP · Parceiro: CLYVO VET** · Entrega final (Sprint 4)
> Aplicação web que transforma o histórico de consultas do pet em **cuidado preventivo**: o tutor sabe quando levar o pet ao veterinário e a clínica sabe quem precisa ser chamado.
 
---
 
## 🔗 Links da entrega
 
| | Link |
|---|---|
| 🌐 **Aplicação online (Render)** | **COLE AQUI O LINK DO RENDER** <br> _ex.: https://petpath-saude.onrender.com_ |
| 🎬 **Vídeo de apresentação** | **COLE AQUI O LINK DO VÍDEO** <br> _ex.: https://youtu.be/..._ |
| 📁 Repositório | **COLE AQUI O LINK DO GITHUB** |
| 🎬 Vídeo do Challenge 3 (anterior) | https://youtu.be/6822tHq40-Y |
 
> ⏱️ A aplicação está no plano gratuito do Render: se ficar 15 minutos sem acesso, ela "dorme". O primeiro acesso pode levar **cerca de 1 minuto**; depois fica normal.
 
---
 
## 👥 Integrantes
 
| Nome | RM |
|---|---|
| Eduardo Augusto de Oliveira Souza | RM565269 |
| Fellipe Costa de Oliveira | RM564673 |
| Felype Ferreira Maschio | RM563009 |
| Gustavo Vieira de Matos | RM563304 |
| Pedro Henrique dos Santos Costa | RM562156 |
 
---
 

 
## 🩺 O problema
 
Na CLYVO VET, como na maioria das clínicas, o pet costuma ser levado ao veterinário **só quando já está doente**. O tutor não sabe de quanto em quanto tempo o pet precisa de check-up (isso muda conforme a **espécie** e a **idade**), e a clínica não tem um jeito simples de saber **quais pacientes estão sem acompanhamento**. O resultado é menos prevenção, mais emergências e mais custos.
 
## 💡 A solução
 
O **PetPath Saúde** tem dois perfis de acesso:
 
| Perfil | O que pode fazer |
|---|---|
| 👤 **Tutor** | Cadastra os pets, agenda e cancela consultas e acompanha o **Plano Preventivo** de cada pet. |
| 🏥 **Clínica (ADMIN)** | Vê os indicadores e a agenda do dia, registra atendimentos com observações clínicas e usa o **Radar de Prevenção** para saber quem contatar. |
 
### ⭐ Diferencial: Plano Preventivo + Radar de Prevenção
 
1. A **espécie** define os marcos de idade (cachorro é idoso a partir de 7 anos, gato a partir de 10, hamster a partir de 2...).
2. A **faixa etária** define o intervalo ideal de check-up: **filhote a cada 3 meses**, **adulto a cada 12** e **idoso a cada 6**.
3. Somando a **última consulta realizada** ao intervalo, o sistema calcula o **próximo check-up sugerido**.
4. Com isso, cada pet fica em uma situação: 🟢 **Em dia**, 🟡 **Check-up em breve** (vence em até 30 dias), 🔴 **Atrasado** ou 🔴 **Sem histórico**.
5. Os pets que precisam de atenção **e não têm consulta marcada** entram no **Radar de Prevenção** da clínica, com o telefone e o e-mail do tutor.
O plano é recalculado automaticamente sempre que a clínica registra uma consulta como realizada.
 
> Os intervalos são uma referência geral por fase da vida. A própria aplicação avisa que a conduta final é sempre do médico-veterinário.
 
---
 
## 🔑 Usuários de teste
 
Os usuários abaixo são criados automaticamente quando a aplicação sobe (migrations `V4` e `V5` do Flyway).
 
| Perfil | Usuário | Senha | Pets e cenário para demonstrar |
|---|---|---|---|
| 🏥 **Clínica (ADMIN)** | `admin` | `admin123` | Vê tudo. O Radar de Prevenção mostra **Mia** (atrasada), **Thor** (sem histórico) e **Bidu** (check-up em breve). |
| 👤 Tutora | `mariasilva` | `tutor123` | **Rex**: cachorro adulto, check-up atrasado, mas com consulta já agendada. **Mia**: gata idosa, check-up atrasado. |
| 👤 Tutor | `joaopereira` | `tutor123` | **Thor**: filhote, sem nenhuma consulta. **Luna**: gata adulta, em dia, com consulta marcada para **hoje às 16:00**. |
| 👤 Tutora | `anacosta` | `tutor123` | **Bidu**: coelho, check-up vence em poucos dias. **Pipoca**: hamster, em dia. |
 
➕ Também é possível **criar um tutor novo** pela tela de cadastro (link "Cadastre-se como tutor", na tela de login).
 
> 📅 As datas dos dados de teste são **relativas ao dia em que a aplicação é iniciada**, então os cenários acima aparecem sempre iguais, em qualquer dia.
> 🔄 O banco é **em memória**: ao reiniciar a aplicação, todos os dados voltam ao estado inicial.
 
---
 
## ▶️ Como rodar o projeto
 
### Pré-requisitos
 
| Item | Versão | Como verificar |
|---|---|---|
| **Java (JDK)** | **17 ou superior** | `java -version` |
| Maven | **não precisa instalar**: o projeto já vem com o Maven Wrapper (`mvnw`) | — |
| Git | qualquer versão (só para clonar) | `git --version` |
 
> Não é preciso instalar nenhum banco de dados: o projeto usa **H2 em memória**, criado e populado automaticamente pelo **Flyway**.
 
### Opção 1: Pelo terminal (recomendado)
 
**1. Clone o repositório e entre na pasta do projeto Java:**
 
```bash
git clone COLE_AQUI_O_LINK_DO_GITHUB
cd challenge4-java/pet_saude
```
 
> Se você baixou o `.zip`, extraia e abra o terminal dentro da pasta **`pet_saude`** (a que tem o arquivo `pom.xml`).
 
**2. Suba a aplicação:**
 
| Sistema | Comando |
|---|---|
| 🪟 Windows (CMD ou PowerShell) | `mvnw.cmd spring-boot:run` |
| 🐧 Linux / 🍎 macOS | `./mvnw spring-boot:run` |
 
> No Linux/macOS, se aparecer "permission denied", rode antes: `chmod +x mvnw`
> Na primeira execução, o Maven baixa as dependências; isso pode levar alguns minutos.
 
**3. Aguarde esta mensagem no console:**
 
```
Started PetSaudeApplication in X seconds
```
 
**4. Acesse no navegador:** 👉 **http://localhost:8080**
 
Você será levado para a tela de login. Use um dos [usuários de teste](#-usuários-de-teste).
 
**5. Para parar a aplicação:** pressione `Ctrl + C` no terminal.
 
### Opção 2: Pelo IntelliJ IDEA
 
1. `File → Open` e selecione a pasta **`pet_saude`**.
2. Aguarde o IntelliJ importar o projeto Maven (barra de progresso no canto inferior).
3. Confira se o projeto está usando o **JDK 17+** em `File → Project Structure → Project → SDK`.
4. Abra `src/main/java/br/com/fiap/pet_saude/PetSaudeApplication.java` e clique no botão ▶️ ao lado de `main`.
5. Acesse **http://localhost:8080**.
### Opção 3: Pelo Eclipse / Spring Tool Suite
 
1. `File → Import → Maven → Existing Maven Projects`.
2. Selecione a pasta **`pet_saude`** e clique em **Finish**.
3. Clique com o botão direito no projeto e escolha `Run As → Spring Boot App` (ou `Java Application` na classe `PetSaudeApplication`).
4. Acesse **http://localhost:8080**.
### Opção 4: Com Docker
 
```bash
cd pet_saude
docker build -t petpath-saude .
docker run -p 8080:8080 petpath-saude
```
 
Acesse **http://localhost:8080**. (No Docker a aplicação roda com o perfil `prod`, então o console do H2 fica desativado.)
 
---
  
## 🛠️ Tecnologias
 
| Tecnologia | Uso / justificativa |
|---|---|
| **Java 17** | Linguagem da disciplina (versão LTS) |
| **Spring Boot 4.1** | Configuração automática, servidor embutido e injeção de dependências |
| **Spring Web MVC + Thymeleaf** | Páginas renderizadas no servidor, com fragmentos reutilizáveis |
| **Spring Data JPA** | Repositórios com *query methods* e JPQL |
| **H2 Database** | Banco em memória: roda sem instalar nada, ideal para demonstração e deploy gratuito |
| **Flyway** | Versionamento do banco: o schema e os dados de teste são recriados de forma idêntica em qualquer máquina |
| **Spring Security** | Login, senha com BCrypt, perfis ADMIN/TUTOR e proteção CSRF |
| **Bean Validation** | Validação dos formulários com mensagens em português |
| **Bootstrap 5 + Bootstrap Icons + CSS próprio** | Interface responsiva com a identidade visual do projeto |
| **JUnit 5, Mockito, MockMvc** | Testes unitários e de integração |
| **Docker + Render** | Deploy na nuvem |
 

 
## 🔄 Evolução: Challenge 3 → Challenge 4
 
* **Novas funcionalidades:** Plano Preventivo por espécie e idade, Radar de Prevenção, agenda do dia, histórico na ficha do pet, filtro de consultas por status, busca de pets e tutores, inativação de pets com histórico, observações clínicas no atendimento.
* **Interface:** layout totalmente redesenhado (menu responsivo, cards, badges, mensagens de sucesso, modais de confirmação, estados vazios, acentuação correta).
* **Qualidade de código:** injeção por construtor, camada de serviço completa, DTO de cadastro, exceções de domínio, fragmentos reutilizáveis, remoção por `POST` e tratamento global de erros.
* **Correções:**
  - Remover um pet com consultas gerava erro 500.
  - O cadastro aceitava usuário e senha vazios.
  - Não era possível atualizar consultas de datas passadas.
* **Deploy:** Dockerfile, perfil `prod` e configuração para o Render.
