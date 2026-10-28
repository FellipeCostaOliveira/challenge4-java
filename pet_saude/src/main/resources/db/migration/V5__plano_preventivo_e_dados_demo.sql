-- Sprint 4: garante unicidade de CPF/e-mail do tutor e amplia a massa de dados
-- para demonstrar o Plano Preventivo e o Radar de Prevenção.
-- As datas são relativas a CURRENT_DATE, assim a demonstração mostra sempre
-- os mesmos cenários (em dia, check-up em breve, atrasado, sem histórico),
-- independentemente do dia em que a aplicação é iniciada.
-- Senha de todos os tutores de demonstração: tutor123

alter table tutor add constraint uk_tutor_cpf unique (cpf);
alter table tutor add constraint uk_tutor_email unique (email);

-- ---------------------------------------------------------------- tutores
insert into tutor (nome, cpf, email, telefone) values ('João Pereira', '12345678909', 'joao@petpath.com', '11988887777');
insert into tutor (nome, cpf, email, telefone) values ('Ana Costa', '98765432100', 'ana@petpath.com', '11977776666');

insert into usuario (username, senha, img_perfil, nome_perfil, fk_tutor)
select 'joaopereira', '$2b$10$fumA6t/qrz2mYsQzt8yWtu4lrB4uEACR6d6VhrItcjmg0IbaxkWu2', null, 'João Pereira', t.id
from tutor t where t.email = 'joao@petpath.com';

insert into usuario (username, senha, img_perfil, nome_perfil, fk_tutor)
select 'anacosta', '$2b$10$fumA6t/qrz2mYsQzt8yWtu4lrB4uEACR6d6VhrItcjmg0IbaxkWu2', null, 'Ana Costa', t.id
from tutor t where t.email = 'ana@petpath.com';

insert into usuario_papel_tab (id_usuario, id_papel)
select u.id, p.id from usuario u, papel p
where u.username in ('joaopereira', 'anacosta') and p.descricao = 'TUTOR';

-- ---------------------------------------------------------------- pets
-- Mia: gata idosa (12 anos), check-up semestral vencido -> ATRASADO (aparece no radar)
insert into pet (fk_tutor, nome, especie, raca, data_nascimento, peso, ativo)
select t.id, 'Mia', 'GATO', 'Siamês', dateadd(YEAR, -12, current_date), 4.2, true
from tutor t where t.email = 'maria@petpath.com';

-- Thor: filhote de 5 meses sem nenhuma consulta -> SEM HISTÓRICO (aparece no radar)
insert into pet (fk_tutor, nome, especie, raca, data_nascimento, peso, ativo)
select t.id, 'Thor', 'CACHORRO', 'Golden Retriever', dateadd(MONTH, -5, current_date), 9.8, true
from tutor t where t.email = 'joao@petpath.com';

-- Luna: gata adulta com check-up recente -> EM DIA; tem consulta hoje (agenda do dia)
insert into pet (fk_tutor, nome, especie, raca, data_nascimento, peso, ativo)
select t.id, 'Luna', 'GATO', 'SRD', dateadd(YEAR, -3, current_date), 3.9, true
from tutor t where t.email = 'joao@petpath.com';

-- Bidu: coelho adulto, check-up anual vence em poucos dias -> CHECK-UP EM BREVE (radar)
insert into pet (fk_tutor, nome, especie, raca, data_nascimento, peso, ativo)
select t.id, 'Bidu', 'COELHO', 'Mini Lop', dateadd(YEAR, -2, current_date), 1.8, true
from tutor t where t.email = 'ana@petpath.com';

-- Pipoca: hamster com consulta recente -> EM DIA
insert into pet (fk_tutor, nome, especie, raca, data_nascimento, peso, ativo)
select t.id, 'Pipoca', 'HAMSTER', 'Sírio', dateadd(MONTH, -14, current_date), 0.15, true
from tutor t where t.email = 'ana@petpath.com';

-- ---------------------------------------------------------------- consultas
insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status, observacoes)
select pe.id, dateadd(MONTH, -8, current_date), time '10:00:00', 'PREVENTIVA', 'Check-up geral e exames de sangue', 'REALIZADA',
       'Função renal levemente alterada. Reavaliar em 6 meses.'
from pet pe where pe.nome = 'Mia';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status, observacoes)
select pe.id, dateadd(MONTH, -2, current_date), time '15:30:00', 'PREVENTIVA', 'Vacina quádrupla felina (reforço anual)', 'REALIZADA',
       'Vacina aplicada. Sem reações.'
from pet pe where pe.nome = 'Luna';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status)
select pe.id, current_date, time '16:00:00', 'BEM_ESTAR', 'Avaliação odontológica', 'AGENDADA'
from pet pe where pe.nome = 'Luna';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status, observacoes)
select pe.id, dateadd(DAY, -350, current_date), time '09:30:00', 'PREVENTIVA', 'Check-up anual e corte de unhas', 'REALIZADA',
       'Peso ideal. Manter dieta rica em feno.'
from pet pe where pe.nome = 'Bidu';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status)
select pe.id, dateadd(DAY, -20, current_date), time '11:00:00', 'TERAPEUTICA', 'Coceira nas orelhas', 'CANCELADA'
from pet pe where pe.nome = 'Bidu';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status, observacoes)
select pe.id, dateadd(MONTH, -1, current_date), time '14:00:00', 'PREVENTIVA', 'Avaliação de rotina', 'REALIZADA',
       'Dentes em bom estado.'
from pet pe where pe.nome = 'Pipoca';
