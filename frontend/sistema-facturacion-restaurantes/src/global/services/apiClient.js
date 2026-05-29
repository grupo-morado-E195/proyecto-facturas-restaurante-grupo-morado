import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://127.0.0.1:5050/api/",
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor de request: agrega el token JWT en cada petición
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor de response: si recibe 401, limpia localStorage y redirige al login
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const isLoginRequest = error.config?.url?.includes("auth/login");
      const isOnLoginPage = window.location.pathname === "/login" || window.location.pathname.endsWith("/login");
      
      if (!isLoginRequest && !isOnLoginPage) {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("sfr_user");
        localStorage.removeItem("sfr_token");
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
