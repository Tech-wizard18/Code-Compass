const BASE_URL = import.meta.env.VITE_API_URL

// Core request function — every API call goes through this
async function request(endpoint, options = {}) {
  const token = localStorage.getItem('token')

  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }

  // Attach the JWT automatically if we have one
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  })

  // Try to parse JSON either way (error responses often have a message body too)
  let data = null
  try {
    data = await response.json()
  } catch {
    // no JSON body, that's fine for some responses
  }

  if (!response.ok) {
    const message = data?.message || data?.error || `Request failed (${response.status})`
    throw new Error(message)
  }

  return data
}

// Auth
export function login(usernameOrEmail, password) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ usernameOrEmail, password }),
  })
}

export function register(email, username, password) {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ email, username, password }),
  })
}

// Repos
export function indexRepo(repoUrl) {
  return request('/api/repos/index', {
    method: 'POST',
    body: JSON.stringify({ repoUrl }),
  })
}

// Chat
export function sendChatMessage(repoUrl, question, conversationId) {
  return request('/api/chat', {
    method: 'POST',
    body: JSON.stringify({ repoUrl, question, conversationId }),
  })
}

// Conversations
export function getConversations() {
  return request('/api/conversations')
}

export function getConversationMessages(id) {
  return request(`/api/conversations/${id}/messages`)
}