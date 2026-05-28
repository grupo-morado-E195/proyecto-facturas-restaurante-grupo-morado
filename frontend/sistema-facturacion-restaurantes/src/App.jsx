import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './global/context/AuthContext.jsx'
import AppRouter from './global/router/AppRouter.jsx'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </BrowserRouter>
  )
}
