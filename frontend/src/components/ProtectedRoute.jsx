import { Navigate } from 'react-router-dom'
import { isTokenExpired } from '../utils/auth'

function ProtectedRoute({ children }) {
  const token = localStorage.getItem('token')

  if (!token || isTokenExpired(token)) {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('activeRepoUrl')
    localStorage.removeItem('activeConversationId')
    return <Navigate to="/login" />
  }

  return children
}

export default ProtectedRoute