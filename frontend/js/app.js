(function () {

  const navigation = document.getElementById('nav');
  const form = document.getElementById('form');
  const apiKeyInput = document.getElementById('apikey');
  const apiKeyState = document.getElementById('keystate');

  let currentOperation = 'create';

  const field = id => document.getElementById(id).value.trim();

  apiKeyInput.addEventListener('input', () => {
    const isSet = apiKeyInput.value.trim().length > 0;
    apiKeyState.textContent = isSet ? 'definida' : 'ausente';
    apiKeyState.classList.toggle('on', isSet);
  });

  navigation.addEventListener('click', event => {
    const button = event.target.closest('button[data-op]');
    if (!button) return;

    currentOperation = button.dataset.op;

    navigation.querySelectorAll('button').forEach(item =>
      item.setAttribute('aria-current', String(item === button)));

    renderPanel();
    Render.clearStatus();
    Render.idle();
  });

  function renderPanel() {
    const operation = OPERATIONS[currentOperation];
    document.getElementById('op-title').textContent = operation.title;
    document.getElementById('op-desc').textContent = operation.description;
    document.getElementById('op-route').textContent = operation.route;
    form.innerHTML = operation.form;
  }

  const handlers = {
    create: createPerson,
    list: () => listPeople(),
    find: findPerson,
    nationality: findNationality,
    remove: removePerson
  };

  form.addEventListener('submit', event => {
    event.preventDefault();
    run(handlers[currentOperation]);
  });

  async function run(operation) {
    const submitButton = form.querySelector('button.go');
    if (submitButton) submitButton.disabled = true;

    try {
      await operation();
    } catch (error) {
      if (error instanceof NetworkError) Render.networkError();
      else throw error;
    } finally {
      if (submitButton) submitButton.disabled = false;
    }
  }

  function accept(response) {
    Render.setStatus(response.status, response.statusText);
    if (!response.ok) {
      Render.error(response.body);
      return false;
    }
    return true;
  }

  function unexpected() {
    Render.message('A API respondeu em um formato inesperado para esta operação.');
  }

  function requireCpf(inputId) {
    const cpf = field(inputId);
    if (cpf) return cpf;

    Render.setStatus(400, 'Bad Request');
    Render.error({
      message: 'Erro de validação nos dados enviados.',
      errors: [{ field: 'cpf', message: 'O CPF é obrigatório.' }]
    });
    return null;
  }

  async function createPerson() {
    const response = await Api.createPerson({
      cpf: field('create-cpf'),
      firstName: field('create-first-name'),
      lastName: field('create-last-name'),
      email: field('create-email')
    });

    if (!accept(response)) return;
    if (!isPerson(response.body)) return unexpected();

    Render.personCreated(response.body);
    form.reset();
  }

  async function listPeople(requestedPage) {
    const page = requestedPage ?? Math.max(1, Number(field('list-page')) || 1);
    const size = Math.min(
      CONFIG.maxPageSize,
      Math.max(1, Number(field('list-size')) || CONFIG.defaultPageSize)
    );

    const response = await Api.listPeople(page, size);
    if (!accept(response)) return;

    const body = response.body;
    const people = Array.isArray(body) ? body : (body?.data ?? body?.content ?? []);

    if (!people.length) {
      Render.idle('Nenhuma pessoa cadastrada nesta página.');
      return;
    }

    let html = Render.peopleTable(people);

    if (!Array.isArray(body)) {
      html += Render.pager(
        body.page ?? page,
        body.totalPages ?? 1,
        body.totalElements ?? people.length
      );
    }

    Render.output().innerHTML = html;
    bindPager();
  }

  function bindPager() {
    Render.output().querySelectorAll('.pager button[data-page]').forEach(button =>
      button.addEventListener('click', () => {
        const page = Number(button.dataset.page);
        document.getElementById('list-page').value = page;
        run(() => listPeople(page));
      }));
  }

  async function findPerson() {
    const cpf = requireCpf('find-cpf');
    if (!cpf) return;

    const response = await Api.findPersonByCpf(cpf);
    if (!accept(response)) return;
    if (!isPerson(response.body)) return unexpected();

    Render.output().innerHTML = Render.peopleTable([response.body]);
  }

  async function findNationality() {
    const cpf = requireCpf('nationality-cpf');
    if (!cpf) return;

    const response = await Api.findNationalityByCpf(cpf);
    if (!accept(response)) return;
    if (!response.body?.nationality) return unexpected();

    Render.nationality(response.body.nationality);
  }

  async function removePerson() {
    const cpf = requireCpf('remove-cpf');
    if (!cpf) return;

    const response = await Api.deletePersonByCpf(cpf);
    if (!accept(response)) return;

    Render.success('Pessoa excluída.');
    form.reset();
  }

  renderPanel();
})();
