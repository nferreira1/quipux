const OPERATIONS = {

  create: {
    title: 'Cadastrar pessoa',
    description: 'Registra uma nova pessoa. Requer a chave de API.',
    route: 'POST /registrarName',
    form: `
      <div class="grid">
        <div class="field">
          <label for="create-cpf">CPF</label>
          <input id="create-cpf" class="mono" maxlength="11" placeholder="11144477735">
          <small>11 dígitos, sem pontuação.</small>
        </div>
        <div class="field">
          <label for="create-email">E-mail</label>
          <input id="create-email" type="email" placeholder="maria.souza@exemplo.com">
        </div>
        <div class="field">
          <label for="create-first-name">Nome</label>
          <input id="create-first-name" placeholder="Maria">
        </div>
        <div class="field">
          <label for="create-last-name">Sobrenome</label>
          <input id="create-last-name" placeholder="Souza">
        </div>
      </div>
      <div class="actions">
        <button class="go" type="submit">Cadastrar</button>
      </div>`
  },

  list: {
    title: 'Listar pessoas',
    description: 'Retorna as pessoas cadastradas de forma paginada.',
    route: 'GET /list',
    form: `
      <div class="grid">
        <div class="field">
          <label for="list-page">Página</label>
          <input id="list-page" type="number" min="1" value="1">
        </div>
        <div class="field">
          <label for="list-size">Por página</label>
          <input id="list-size" type="number" min="1" max="100" value="10">
        </div>
      </div>
      <div class="actions">
        <button class="go" type="submit">Listar</button>
      </div>`
  },

  find: {
    title: 'Buscar por CPF',
    description: 'Retorna os dados da pessoa correspondente ao CPF.',
    route: 'GET /list/{cpf}',
    form: `
      <div class="field span">
        <label for="find-cpf">CPF</label>
        <input id="find-cpf" class="mono" maxlength="11" placeholder="11144477735">
      </div>
      <div class="actions">
        <button class="go" type="submit">Buscar</button>
      </div>`
  },

  nationality: {
    title: 'Prever nacionalidade',
    description: 'Consulta a nationalize.io a partir do nome da pessoa e retorna o país mais provável.',
    route: 'GET /findNacionalityByPerson/{cpf}',
    form: `
      <div class="field span">
        <label for="nationality-cpf">CPF</label>
        <input id="nationality-cpf" class="mono" maxlength="11" placeholder="11144477735">
      </div>
      <div class="actions">
        <button class="go" type="submit">Consultar</button>
      </div>`
  },

  remove: {
    title: 'Excluir pessoa',
    description: 'Remove a pessoa correspondente ao CPF. Requer a chave de API.',
    route: 'DELETE /list/{cpf}',
    form: `
      <div class="field span">
        <label for="remove-cpf">CPF</label>
        <input id="remove-cpf" class="mono" maxlength="11" placeholder="11144477735">
      </div>
      <div class="actions">
        <button class="go danger" type="submit">Excluir</button>
        <span class="hint">A exclusão não pode ser desfeita.</span>
      </div>`
  }
};
