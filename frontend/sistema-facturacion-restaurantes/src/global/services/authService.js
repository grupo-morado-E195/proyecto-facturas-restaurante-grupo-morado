const MOCK_USERS = [
  { id: 1, nombre: "Juan García",      email: "admin@sfr.com",   password: "admin123",   rol: "admin"  },
  { id: 2, nombre: "Laura Martínez",   email: "mesero@sfr.com",  password: "mesero123",  rol: "mesero" },
  { id: 3, nombre: "Carlos Rodríguez", email: "chef@sfr.com",    password: "chef123",    rol: "chef"   },
  { id: 4, nombre: "Ana López",        email: "cajero@sfr.com",  password: "cajero123",  rol: "cajero" },
];

export async function login(email, password) {
  await new Promise((r) => setTimeout(r, 600));

  const found = MOCK_USERS.find(
    (u) => u.email === email && u.password === password,
  );

  if (!found) {
    throw new Error("Credenciales incorrectas");
  }

  const { password: _, ...user } = found;
  const token = `mock-token-${user.id}-${Date.now()}`;

  return { user, token };
}

export async function logout() {
  await new Promise((r) => setTimeout(r, 200));
}
