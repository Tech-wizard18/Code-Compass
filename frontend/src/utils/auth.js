export function isTokenExpired(token) {
  try {
    const payload = token.split('.')[1]
    const decoded = JSON.parse(atob(payload))
    const expiryInSeconds = decoded.exp
    const nowInSeconds = Date.now() / 1000
    return expiryInSeconds < nowInSeconds
  } catch {
    // if we can't decode it at all, treat it as expired/invalid
    return true
  }
}