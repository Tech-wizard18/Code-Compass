import { useState, useRef, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import { indexRepo, sendChatMessage, getConversationMessages } from '../api'
import Navbar from '../components/Navbar'
import Sidebar from '../components/Sidebar'

function cleanAnswerText(text) {
  return text.replace(
    /[A-Za-z]:\\[^`\s]*?codecompass-repo-\d+\\(.+?)(?=[`\s]|$)/g,
    (match, afterRepoId) => afterRepoId.replace(/\\/g, '/')
  )
}

function parseCitations(citations) {
  if (!citations) return null
  if (Array.isArray(citations)) return citations
  if (typeof citations === 'string') {
    try {
      return JSON.parse(citations)
    } catch {
      return null
    }
  }
  return null
}

function Citations({ citations }) {
  const [open, setOpen] = useState(false)

  if (!citations || citations.length === 0) return null

  const cleanPath = (fullPath) => {
    const normalized = fullPath.replace(/\\/g, '/')
    const srcIndex = normalized.lastIndexOf('/src/')
    return srcIndex !== -1 ? normalized.slice(srcIndex + 1) : normalized.split('/').pop()
  }

  return (
    <div className="mt-2">
      <button
        onClick={() => setOpen(!open)}
        className="text-xs text-stone-400 hover:text-stone-600 transition flex items-center gap-1"
      >
        <span>{open ? '▾' : '▸'}</span>
        {citations.length === 1 ? '1 source' : `${citations.length} sources`}
      </button>

      {open && (
        <div className="mt-2 space-y-1.5">
          {citations.map((c, i) => (
            <div
              key={i}
              className="text-xs font-mono bg-stone-100 border border-stone-200 rounded-lg px-3 py-1.5 text-stone-600"
            >
              {cleanPath(c.filePath)}
              {c.name && <span className="text-stone-500"> · {c.name}</span>}
              {c.startLine != null && (
                <span className="text-stone-400">
                  {' '}(lines {c.startLine}
                  {c.endLine != null && c.endLine !== c.startLine ? `–${c.endLine}` : ''})
                </span>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

function Chat() {
  const [inputUrl, setInputUrl] = useState('')
  const [repoUrl, setRepoUrl] = useState('')
  const [indexing, setIndexing] = useState(false)
  const [messages, setMessages] = useState([])
  const [question, setQuestion] = useState('')
  const [conversationId, setConversationId] = useState(null)
  const [sending, setSending] = useState(false)
  const [restoring, setRestoring] = useState(true)
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const lastUserMsgRef = useRef(null)

  useEffect(() => {
    lastUserMsgRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [messages])

  useEffect(() => {
    const savedRepoUrl = localStorage.getItem('activeRepoUrl')
    const savedConversationId = localStorage.getItem('activeConversationId')

    if (savedConversationId) {
      getConversationMessages(savedConversationId)
        .then((msgs) => {
          setMessages(
            msgs.map((m) => ({
              role: m.role,
              content: m.content,
              citations: parseCitations(m.citations),
            }))
          )
          setConversationId(Number(savedConversationId))
          setRepoUrl(savedRepoUrl)
        })
        .catch(() => {
          localStorage.removeItem('activeRepoUrl')
          localStorage.removeItem('activeConversationId')
        })
        .finally(() => setRestoring(false))
    } else if (savedRepoUrl) {
      setRepoUrl(savedRepoUrl)
      setRestoring(false)
    } else {
      setRestoring(false)
    }
  }, [])

  const handleIndexRepo = async (e) => {
    e.preventDefault()
    setIndexing(true)

    try {
      await indexRepo(inputUrl)
      setRepoUrl(inputUrl)
      localStorage.setItem('activeRepoUrl', inputUrl)
      toast.success('Repository indexed!')
    } catch (err) {
      toast.error(err.message)
    } finally {
      setIndexing(false)
    }
  }

  const handleSendMessage = async (e) => {
    e.preventDefault()
    const askedQuestion = question
    setQuestion('')
    setSending(true)

    try {
      const data = await sendChatMessage(repoUrl, askedQuestion, conversationId)

      setMessages([
        ...messages,
        { role: 'user', content: askedQuestion },
        { role: 'assistant', content: data.answer, citations: data.citations },
      ])
      setConversationId(data.conversationId)
      localStorage.setItem('activeConversationId', data.conversationId)
      localStorage.setItem('activeRepoUrl', repoUrl)
    } catch (err) {
      toast.error(err.message)
    } finally {
      setSending(false)
    }
  }

  const handleSelectConversation = async (conv) => {
    try {
      const msgs = await getConversationMessages(conv.id)
      setMessages(
        msgs.map((m) => ({
          role: m.role,
          content: m.content,
          citations: parseCitations(m.citations),
        }))
      )
      setConversationId(conv.id)
      setRepoUrl(conv.repoUrl)
      localStorage.setItem('activeConversationId', conv.id)
      localStorage.setItem('activeRepoUrl', conv.repoUrl)
    } catch (err) {
      toast.error(err.message)
    }
  }

  const handleNewChat = () => {
    setMessages([])
    setConversationId(null)
    localStorage.removeItem('activeConversationId')
  }

  const handleActiveConversationDeleted = () => {
    setMessages([])
    setConversationId(null)
    localStorage.removeItem('activeConversationId')
  }

  const handleIndexAnotherRepo = () => {
    setRepoUrl('')
    setMessages([])
    setConversationId(null)
    setInputUrl('')
    localStorage.removeItem('activeRepoUrl')
    localStorage.removeItem('activeConversationId')
  }

  if (restoring) {
    return (
      <div className="min-h-screen bg-stone-50 flex flex-col">
        <Navbar />
        <div className="flex-1 flex items-center justify-center">
          <p className="text-stone-400 text-sm">Loading...</p>
        </div>
      </div>
    )
  }

  if (!repoUrl) {
    return (
      <div className="h-screen flex flex-col bg-stone-50">
        <Navbar onMenuClick={() => setSidebarOpen(true)} />
        <div className="flex-1 flex overflow-hidden">
          <Sidebar
            onSelectConversation={handleSelectConversation}
            activeConversationId={conversationId}
            isOpen={sidebarOpen}
            onClose={() => setSidebarOpen(false)}
            onActiveConversationDeleted={handleActiveConversationDeleted}
          />
          <div className="flex-1 flex items-center justify-center px-4">
            <div className="w-full max-w-md bg-white border border-stone-200 rounded-2xl shadow-sm p-8">
              <h1 className="text-2xl font-bold text-stone-900 mb-1">
                Explore a repository
              </h1>
              <p className="text-stone-500 text-sm mb-6">
                Paste a public GitHub repo URL to get started
              </p>

              <form onSubmit={handleIndexRepo} className="space-y-4">
                <input
                  type="text"
                  value={inputUrl}
                  onChange={(e) => setInputUrl(e.target.value)}
                  placeholder="https://github.com/owner/repo"
                  className="w-full px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  required
                />
                <button
                  type="submit"
                  disabled={indexing}
                  className="w-full bg-indigo-600 text-white font-medium py-2 rounded-lg hover:bg-indigo-700 transition disabled:opacity-50"
                >
                  {indexing ? 'Indexing repository...' : 'Index Repository'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="h-screen flex flex-col bg-stone-50">
      <Navbar onMenuClick={() => setSidebarOpen(true)} />
      <div className="flex-1 flex overflow-hidden">
        <Sidebar
          onSelectConversation={handleSelectConversation}
          activeConversationId={conversationId}
          onNewChat={handleNewChat}
          onIndexAnotherRepo={handleIndexAnotherRepo}
          isOpen={sidebarOpen}
          onClose={() => setSidebarOpen(false)}
          onActiveConversationDeleted={handleActiveConversationDeleted}
        />
        <div className="flex-1 flex flex-col">
          {/* Messages area */}
          <div className="flex-1 overflow-y-auto">
            <div className="max-w-3xl mx-auto px-4 py-8 space-y-6">
              {messages.map((msg, i) =>
                msg.role === 'user' ? (
                  <div
                    key={i}
                    ref={i === messages.length - 1 ? lastUserMsgRef : null}
                    className="flex justify-end scroll-mt-8"
                  >
                    <div className="bg-stone-200 text-stone-900 rounded-2xl px-4 py-2 max-w-[75%]">
                      {msg.content}
                    </div>
                  </div>
                ) : (
                  <div key={i} className="text-stone-800 leading-relaxed whitespace-pre-wrap">
                    {cleanAnswerText(msg.content)}
                    <Citations citations={msg.citations} />
                  </div>
                )
              )}

              {sending && (
                <div className="space-y-2">
                  <p className="text-sm text-stone-400">CodeCompass is thinking</p>
                  <div className="flex gap-1 items-center text-stone-400">
                    <span className="w-2 h-2 bg-stone-400 rounded-full animate-bounce [animation-delay:-0.3s]"></span>
                    <span className="w-2 h-2 bg-stone-400 rounded-full animate-bounce [animation-delay:-0.15s]"></span>
                    <span className="w-2 h-2 bg-stone-400 rounded-full animate-bounce"></span>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Input bar */}
          <div className="sticky bottom-0 bg-stone-50 border-t border-stone-200 px-4 py-4">
            <form onSubmit={handleSendMessage} className="max-w-3xl mx-auto flex gap-2">
              <input
                type="text"
                value={question}
                onChange={(e) => setQuestion(e.target.value)}
                placeholder="Ask a question about this repo..."
                className="flex-1 px-4 py-3 border border-stone-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm"
                required
              />
              <button
                type="submit"
                disabled={sending}
                className="bg-indigo-600 text-white font-medium px-5 py-3 rounded-xl hover:bg-indigo-700 transition disabled:opacity-50"
              >
                Send
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Chat