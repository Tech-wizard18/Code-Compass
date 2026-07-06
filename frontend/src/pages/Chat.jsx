import { useState, useRef, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import { indexRepo, sendChatMessage } from '../api'

function Chat() {
  const [inputUrl, setInputUrl] = useState('')
  const [repoUrl, setRepoUrl] = useState('')
  const [indexing, setIndexing] = useState(false)
  const [messages, setMessages] = useState([])
  const [question, setQuestion] = useState('')
  const [conversationId, setConversationId] = useState(null)
  const [sending, setSending] = useState(false)

  const lastUserMsgRef = useRef(null)

  useEffect(() => {
    lastUserMsgRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [messages])

  const handleIndexRepo = async (e) => {
    e.preventDefault()
    setIndexing(true)

    try {
      await indexRepo(inputUrl)
      setRepoUrl(inputUrl)
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
    } catch (err) {
      toast.error(err.message)
    } finally {
      setSending(false)
    }
  }

  if (!repoUrl) {
    return (
      <div className="min-h-screen bg-stone-50 flex items-center justify-center px-4">
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
    )
  }

  return (
    <div className="h-screen flex flex-col bg-stone-50">
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
                {msg.content}
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
  )
}

export default Chat