insert into papel (descricao) values ('ADMIN');
insert into papel (descricao) values ('TUTOR');

insert into usuario (username, senha, img_perfil, nome_perfil, fk_tutor)
values ('admin', '$2b$10$hOZdRetHCqFISCK/.Rxu8esErLwcJEYCtDVErZH.KqCU0lFyxRCIy', '/img/logo.svg', 'Equipe CLYVO VET', null);

insert into usuario_papel_tab (id_usuario, id_papel)
select u.id, p.id from usuario u, papel p where u.username = 'admin' and p.descricao = 'ADMIN';

insert into tutor (nome, cpf, email, telefone)
values ('Maria Silva', '52998224725', 'maria@petpath.com', '11999999999');

insert into usuario (username, senha, img_perfil, nome_perfil, fk_tutor)
select 'mariasilva', '$2b$10$fumA6t/qrz2mYsQzt8yWtu4lrB4uEACR6d6VhrItcjmg0IbaxkWu2', '/img/logo.svg', 'Maria Silva', t.id
from tutor t where t.email = 'maria@petpath.com';

insert into usuario_papel_tab (id_usuario, id_papel)
select u.id, p.id from usuario u, papel p where u.username = 'mariasilva' and p.descricao = 'TUTOR';

insert into pet (fk_tutor, nome, especie, raca, data_nascimento, peso, ativo)
select t.id, 'Rex', 'CACHORRO', 'Labrador', date '2021-05-10', 15.5, true
from tutor t where t.email = 'maria@petpath.com';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status, observacoes)
select pe.id, date '2025-03-10', time '09:00:00', 'PREVENTIVA', 'Vacinacao anual antirrabica', 'REALIZADA',
       'Pet saudavel, sem alteracoes.'
from pet pe where pe.nome = 'Rex';

insert into consulta (fk_pet, data_consulta, horario, tipo, descricao, status)
select pe.id, date '2026-12-15', time '14:30:00', 'RETORNO', 'Retorno de rotina', 'AGENDADA'
from pet pe where pe.nome = 'Rex';
