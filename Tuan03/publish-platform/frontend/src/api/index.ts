const BASE = 'http://localhost:8080/api';

function headers(username?: string) {
  const h: Record<string, string> = { 'Content-Type': 'application/json' };
  if (username) h['X-Username'] = username;
  return h;
}

// ── Auth ──────────────────────────────────────────────────────────
export const api = {
  auth: {
    login: (username: string, password: string) =>
      fetch(`${BASE}/auth/login`, { method: 'POST', headers: headers(), body: JSON.stringify({ username, password }) }).then(r => r.json()),

    register: (username: string, email: string, password: string, role = 'WRITER') =>
      fetch(`${BASE}/auth/register`, { method: 'POST', headers: headers(), body: JSON.stringify({ username, email, password, role }) }).then(r => r.json()),
  },

  // ── Users ───────────────────────────────────────────────────────
  users: {
    me: (username: string) =>
      fetch(`${BASE}/user`, { headers: headers(username) }).then(r => r.json()),

    all: (username: string) =>
      fetch(`${BASE}/users`, { headers: headers(username) }).then(r => r.json()),

    updateRole: (id: number, role: string, username: string) =>
      fetch(`${BASE}/users/${id}/role`, { method: 'PUT', headers: headers(username), body: JSON.stringify({ role }) }).then(r => r.json()),
  },

  // ── Articles ────────────────────────────────────────────────────
  articles: {
    create: (title: string, content: string, username: string) =>
      fetch(`${BASE}/articles`, { method: 'POST', headers: headers(username), body: JSON.stringify({ title, content }) }).then(r => r.json()),

    update: (id: number, title: string, content: string, username: string) =>
      fetch(`${BASE}/articles/${id}`, { method: 'PUT', headers: headers(username), body: JSON.stringify({ title, content }) }).then(r => r.json()),

    publish: (id: number, username: string) =>
      fetch(`${BASE}/publish/${id}`, { method: 'POST', headers: headers(username) }).then(r => r.json()),

    archive: (id: number, username: string) =>
      fetch(`${BASE}/archive/${id}`, { method: 'POST', headers: headers(username) }).then(r => r.json()),

    delete: (id: number, username: string) =>
      fetch(`${BASE}/articles/${id}`, { method: 'DELETE', headers: headers(username) }),

    getAll: (username: string) =>
      fetch(`${BASE}/articles`, { headers: headers(username) }).then(r => r.json()),

    getMine: (username: string) =>
      fetch(`${BASE}/articles/mine`, { headers: headers(username) }).then(r => r.json()),

    getPublished: () =>
      fetch(`${BASE}/articles/published`).then(r => r.json()),
  },

  // ── Media ────────────────────────────────────────────────────────
  media: {
    upload: (file: File, username: string) => {
      const form = new FormData();
      form.append('file', file);
      return fetch(`${BASE}/media`, { method: 'POST', headers: { 'X-Username': username }, body: form }).then(r => r.json());
    },

    delete: (id: number, username: string) =>
      fetch(`${BASE}/media/${id}`, { method: 'DELETE', headers: headers(username) }),

    getAll: (username: string) =>
      fetch(`${BASE}/media`, { headers: headers(username) }).then(r => r.json()),

    getMine: (username: string) =>
      fetch(`${BASE}/media/mine`, { headers: headers(username) }).then(r => r.json()),
  },
};
