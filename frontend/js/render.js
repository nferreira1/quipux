const Render = {

  output() {
    return document.getElementById('out');
  },

  statusBadge() {
    return document.getElementById('status');
  },

  escape(value) {
    return String(value ?? '').replace(/[&<>"]/g, character =>
      ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[character]));
  },

  setStatus(status, statusText) {
    const badge = this.statusBadge();
    badge.textContent = status ? `${status} ${statusText ?? ''}`.trim() : statusText;
    badge.className = 'status ' + (status >= 200 && status < 300 ? 'ok' : 'bad');
  },

  clearStatus() {
    const badge = this.statusBadge();
    badge.textContent = 'aguardando';
    badge.className = 'status';
  },

  idle(message = 'Envie uma requisição para ver o resultado.') {
    this.output().innerHTML = `<p class="idle">${this.escape(message)}</p>`;
  },

  message(text) {
    this.output().innerHTML = `<p class="errmsg">${this.escape(text)}</p>`;
  },

  success(text) {
    this.output().innerHTML = `<p class="okmsg">${this.escape(text)}</p>`;
  },

  error(body) {
    if (!body || !body.message) {
      this.message('A API respondeu com um erro sem detalhamento.');
      return;
    }

    let html = `<p class="errmsg">${this.escape(body.message)}</p>`;

    const details = body.errors ?? [];
    if (details.length) {
      html += details.map(detail => `
        <div class="errline">
          <b>${this.escape(detail.field ?? '—')}</b>
          <span>${this.escape(detail.message ?? detail)}</span>
        </div>`).join('');
    }

    this.output().innerHTML = html;
  },

  networkError() {
    this.setStatus(0, 'sem conexão');
    this.output().innerHTML = `
      <p class="errmsg">Não foi possível falar com a API em ${this.escape(CONFIG.baseUrl)}.</p>
      <p class="idle">Verifique se o backend está rodando e se o CORS permite esta origem.</p>`;
  },

  peopleTable(people) {
    const rows = people.map(person => `
      <tr>
        <td class="mono">${this.escape(person.cpf)}</td>
        <td>${this.escape(person.firstName)}</td>
        <td>${this.escape(person.lastName)}</td>
        <td>${this.escape(person.email)}</td>
      </tr>`).join('');

    return `
      <table>
        <thead>
          <tr><th>CPF</th><th>Nome</th><th>Sobrenome</th><th>E-mail</th></tr>
        </thead>
        <tbody>${rows}</tbody>
      </table>`;
  },

  pager(currentPage, totalPages, totalElements) {
    return `
      <div class="pager">
        <button type="button" ${currentPage <= 1 ? 'disabled' : ''} data-page="${currentPage - 1}">Anterior</button>
        <span>Página ${currentPage} de ${totalPages} — ${totalElements} registro(s)</span>
        <button type="button" ${currentPage >= totalPages ? 'disabled' : ''} data-page="${currentPage + 1}">Próxima</button>
      </div>`;
  },

  personCreated(person) {
    this.output().innerHTML =
      `<p class="okmsg">Pessoa cadastrada.</p>` + this.peopleTable([person]);
  },

  nationality(country) {
    this.output().innerHTML = `
      <p class="big-label">País mais provável</p>
      <p class="big">${this.escape(country)}</p>`;
  }
};

function isPerson(body) {
  return Boolean(body && typeof body === 'object' && typeof body.cpf === 'string');
}
