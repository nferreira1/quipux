class NetworkError extends Error {}

const Api = {

  getApiKey() {
    return document.getElementById('apikey').value.trim();
  },

  buildHeaders({ withApiKey = false, withBody = false } = {}) {
    const headers = {};
    if (withBody) headers['Content-Type'] = 'application/json';
    if (withApiKey) headers[CONFIG.apiKeyHeader] = this.getApiKey();
    return headers;
  },

  async request(path, options = {}) {
    let response;
    try {
      response = await fetch(`${CONFIG.baseUrl}${path}`, options);
    } catch (cause) {
      throw new NetworkError('Falha ao conectar na API.', { cause });
    }

    let body = null;
    if (response.status !== 204) {
      try {
        body = await response.json();
      } catch {
        body = null;
      }
    }

    return { status: response.status, statusText: response.statusText, body, ok: response.ok };
  },

  createPerson(person) {
    return this.request('/registrarName', {
      method: 'POST',
      headers: this.buildHeaders({ withApiKey: true, withBody: true }),
      body: JSON.stringify(person)
    });
  },

  listPeople(page, size) {
    return this.request(`/list?page=${page}&size=${size}`);
  },

  findPersonByCpf(cpf) {
    return this.request(`/list/${encodeURIComponent(cpf)}`);
  },

  findNationalityByCpf(cpf) {
    return this.request(`/findNacionalityByPerson/${encodeURIComponent(cpf)}`);
  },

  deletePersonByCpf(cpf) {
    return this.request(`/list/${encodeURIComponent(cpf)}`, {
      method: 'DELETE',
      headers: this.buildHeaders({ withApiKey: true })
    });
  }
};
