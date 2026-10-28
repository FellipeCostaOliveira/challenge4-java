/*
 * PetPath Saúde - comportamentos de interface.
 * 1) Formulários com data-confirmar="mensagem" pedem confirmação num modal
 *    (substitui o window.confirm, que tem aparência nativa e quebra a identidade visual).
 * 2) O modal "Registrar consulta realizada" recebe a URL da consulta escolhida.
 */
(function () {
  'use strict';

  const modalConfirmacaoEl = document.getElementById('modalConfirmacao');
  if (modalConfirmacaoEl) {
    const modal = new bootstrap.Modal(modalConfirmacaoEl);
    const textoEl = modalConfirmacaoEl.querySelector('[data-texto-confirmacao]');
    const botaoEl = modalConfirmacaoEl.querySelector('[data-botao-confirmacao]');
    let formularioPendente = null;

    document.querySelectorAll('form[data-confirmar]').forEach(function (form) {
      form.addEventListener('submit', function (evento) {
        evento.preventDefault();
        formularioPendente = form;
        textoEl.textContent = form.dataset.confirmar;
        botaoEl.textContent = form.dataset.confirmarBotao || 'Confirmar';
        modal.show();
      });
    });

    botaoEl.addEventListener('click', function () {
      if (formularioPendente) {
        botaoEl.disabled = true;
        formularioPendente.submit();
      }
    });
  }

  const modalRealizarEl = document.getElementById('modalRealizar');
  if (modalRealizarEl) {
    modalRealizarEl.addEventListener('show.bs.modal', function (evento) {
      const origem = evento.relatedTarget;
      modalRealizarEl.querySelector('form').action = origem.dataset.acao;
      modalRealizarEl.querySelector('[data-nome-pet]').textContent = origem.dataset.pet;
      modalRealizarEl.querySelector('textarea').value = '';
    });
  }

  /* Evita duplo envio em qualquer formulário que não use confirmação. */
  document.querySelectorAll('form:not([data-confirmar])').forEach(function (form) {
    form.addEventListener('submit', function () {
      const botao = form.querySelector('button[type="submit"]');
      if (botao) {
        setTimeout(function () { botao.disabled = true; }, 0);
      }
    });
  });
})();
