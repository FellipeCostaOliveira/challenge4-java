alter table if exists usuario
    add constraint fk_usuario_tutor
    foreign key (fk_tutor)
    references tutor;

alter table if exists usuario_papel_tab
    add constraint fk_usuariopapel_usuario
    foreign key (id_usuario)
    references usuario;

alter table if exists usuario_papel_tab
    add constraint fk_usuariopapel_papel
    foreign key (id_papel)
    references papel;

alter table if exists pet
    add constraint fk_pet_tutor
    foreign key (fk_tutor)
    references tutor;

alter table if exists consulta
    add constraint fk_consulta_pet
    foreign key (fk_pet)
    references pet;

create index idx_pet_tutor on pet (fk_tutor);
create index idx_consulta_pet on consulta (fk_pet);
create index idx_consulta_status on consulta (status);
