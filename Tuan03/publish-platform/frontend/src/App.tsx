import { useState, useEffect, useRef } from 'react';
import { api } from './api';

// ── Types ─────────────────────────────────────────────────────────
interface AuthUser { username: string; email: string; role: string; canPublish: boolean; canManageMedia: boolean; canManageUsers: boolean; }
interface Article  { id: number; title: string; content: string; authorUsername: string; status: string; createdAt: string; publishedAt?: string; }
interface MediaFile { id: number; originalName: string; url: string; mediaType: string; fileSize: number; uploadedBy: string; uploadedAt: string; }
interface User      { id: number; username: string; email: string; role: string; }

type Tab = 'articles' | 'media' | 'users';

// ── STATUS BADGE ─────────────────────────────────────────────────
function StatusBadge({ status }: { status: string }) {
  const colors: Record<string, string> = { DRAFT: '#64748b', PUBLISHED: '#16a34a', ARCHIVED: '#9333ea' };
  return <span style={{ fontSize: 11, fontWeight: 600, padding: '2px 8px', borderRadius: 20, background: colors[status] + '22', color: colors[status] }}>{status}</span>;
}

// ── ROLE BADGE ────────────────────────────────────────────────────
function RoleBadge({ role }: { role: string }) {
  const colors: Record<string, string> = { ADMIN: '#ef4444', EDITOR: '#f97316', WRITER: '#6366f1' };
  return <span style={{ fontSize: 11, fontWeight: 600, padding: '2px 8px', borderRadius: 20, background: colors[role] + '22', color: colors[role] }}>{role}</span>;
}

// ════════════════════════════════════════════════════════════════════
// MAIN APP
// ════════════════════════════════════════════════════════════════════
export default function App() {
  const [authUser, setAuthUser] = useState<AuthUser | null>(null);
  const [tab, setTab]           = useState<Tab>('articles');
  const [mode, setMode]         = useState<'login' | 'register'>('login'); // Toggle login/register
  
  // Login form
  const [loginForm, setLoginForm] = useState({ username: '', password: '' });
  const [loginError, setLoginError] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  // Register form
  const [regForm, setRegForm] = useState({ username: '', email: '', password: '', confirmPassword: '' });
  const [regError, setRegError] = useState('');
  const [regLoading, setRegLoading] = useState(false);

  const handleLogin = async () => {
    setLoginError(''); setLoginLoading(true);
    try {
      const data = await api.auth.login(loginForm.username, loginForm.password);
      if (data.error) { setLoginError(data.error); return; }
      setAuthUser(data);
      setLoginForm({ username: '', password: '' });
    } catch { setLoginError('Lỗi kết nối server'); }
    finally { setLoginLoading(false); }
  };

  const handleRegister = async () => {
    setRegError(''); setRegLoading(true);
    
    // Validation
    if (!regForm.username.trim()) { setRegError('Username không được rỗng'); setRegLoading(false); return; }
    if (!regForm.email.includes('@')) { setRegError('Email không hợp lệ'); setRegLoading(false); return; }
    if (regForm.password.length < 6) { setRegError('Mật khẩu tối thiểu 6 ký tự'); setRegLoading(false); return; }
    if (regForm.password !== regForm.confirmPassword) { setRegError('Mật khẩu không trùng khớp'); setRegLoading(false); return; }

    try {
      const data = await api.auth.register(regForm.username, regForm.email, regForm.password, 'WRITER');
      if (data.error) { setRegError(data.error); setRegLoading(false); return; }
      setRegError('');
      setMode('login');
      setLoginForm({ username: regForm.username, password: regForm.password });
      setRegForm({ username: '', email: '', password: '', confirmPassword: '' });
    } catch (e) { setRegError('Lỗi tạo tài khoản'); }
    finally { setRegLoading(false); }
  };

  const handleLogout = () => { setAuthUser(null); setLoginForm({ username: '', password: '' }); };
  const toggleMode = () => { setMode(mode === 'login' ? 'register' : 'login'); setLoginError(''); setRegError(''); };

  // ── CSS ─────────────────────────────────────────────────────────
  const css = `
    @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap');
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: 'Plus Jakarta Sans', sans-serif; background: #f8fafc; color: #1e293b; }
    button { font-family: inherit; cursor: pointer; }
    input, textarea, select { font-family: inherit; }
    .container { max-width: 1100px; margin: 0 auto; padding: 0 20px; }

    /* NAV */
    .nav { background: #1e293b; color: #f1f5f9; padding: 0 24px; display: flex; align-items: center; gap: 16px; height: 56px; }
    .nav-brand { font-weight: 700; font-size: 16px; color: #f1f5f9; margin-right: auto; }
    .nav-user { font-size: 13px; color: #94a3b8; }
    .nav-tab { background: transparent; border: none; color: #94a3b8; font-size: 13px; font-weight: 500; padding: 8px 14px; border-radius: 8px; transition: all .15s; }
    .nav-tab:hover { background: rgba(255,255,255,.08); color: #f1f5f9; }
    .nav-tab.active { background: rgba(99,102,241,.25); color: #a5b4fc; }
    .btn-logout { background: rgba(239,68,68,.15); border: none; color: #f87171; padding: 6px 14px; border-radius: 8px; font-size: 12px; font-weight: 600; }

    /* AUTH */
    .auth-wrap { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f1f5f9; }
    .auth-card { background: #fff; border-radius: 16px; padding: 36px 32px; width: 360px; box-shadow: 0 4px 24px rgba(0,0,0,.08); }
    .auth-title { font-size: 22px; font-weight: 700; margin-bottom: 6px; }
    .auth-sub { font-size: 13px; color: #64748b; margin-bottom: 24px; }
    .form-label { font-size: 12px; font-weight: 600; color: #64748b; display: block; margin-bottom: 5px; }
    .form-input { width: 100%; border: 1px solid #e2e8f0; border-radius: 10px; padding: 10px 12px; font-size: 14px; outline: none; transition: border-color .2s; margin-bottom: 14px; }
    .form-input:focus { border-color: #6366f1; }
    .btn-primary { width: 100%; background: #6366f1; color: #fff; border: none; border-radius: 10px; padding: 11px; font-weight: 700; font-size: 14px; transition: background .2s; }
    .btn-primary:hover { background: #4f46e5; }
    .btn-primary:disabled { opacity: .5; cursor: not-allowed; }
    .auth-error { color: #ef4444; font-size: 13px; margin-bottom: 12px; padding: 8px; background: #fef2f2; border-radius: 8px; border: 1px solid #fecaca; }

    /* PAGE */
    .page { padding: 28px 0; }
    .page-title { font-size: 20px; font-weight: 700; margin-bottom: 20px; }

    /* CARD */
    .card { background: #fff; border-radius: 12px; border: 1px solid #e2e8f0; padding: 20px; margin-bottom: 12px; }

    /* ARTICLE EDITOR */
    .editor-grid { display: grid; grid-template-columns: 1fr 360px; gap: 20px; }
    .editor-area { display: flex; flex-direction: column; gap: 12px; }
    .form-textarea { width: 100%; border: 1px solid #e2e8f0; border-radius: 10px; padding: 12px; font-size: 14px; outline: none; resize: vertical; transition: border-color .2s; }
    .form-textarea:focus { border-color: #6366f1; }
    .btn-row { display: flex; gap: 8px; flex-wrap: wrap; }
    .btn { border: none; border-radius: 8px; padding: 8px 16px; font-size: 13px; font-weight: 600; transition: all .15s; }
    .btn-blue  { background: #6366f1; color: #fff; }
    .btn-blue:hover { background: #4f46e5; }
    .btn-green { background: #16a34a; color: #fff; }
    .btn-green:hover { background: #15803d; }
    .btn-red   { background: #fef2f2; color: #ef4444; border: 1px solid #fecaca; }
    .btn-red:hover { background: #fee2e2; }
    .btn-gray  { background: #f1f5f9; color: #64748b; border: 1px solid #e2e8f0; }
    .btn-gray:hover { background: #e2e8f0; }
    .btn:disabled { opacity: .4; cursor: not-allowed; }

    /* ARTICLE LIST */
    .article-item { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; padding: 14px 0; border-bottom: 1px solid #f1f5f9; }
    .article-item:last-child { border-bottom: none; }
    .article-title { font-weight: 600; font-size: 14px; margin-bottom: 4px; }
    .article-meta  { font-size: 12px; color: #94a3b8; display: flex; gap: 10px; align-items: center; }

    /* MEDIA GRID */
    .media-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; }
    .media-item { border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; }
    .media-thumb { width: 100%; height: 110px; object-fit: cover; background: #f1f5f9; display: block; }
    .media-thumb-placeholder { width: 100%; height: 110px; background: #f1f5f9; display: flex; align-items: center; justify-content: center; font-size: 28px; }
    .media-info { padding: 8px 10px; }
    .media-name { font-size: 12px; font-weight: 600; color: #1e293b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .media-size  { font-size: 11px; color: #94a3b8; }
    .upload-zone { border: 2px dashed #cbd5e1; border-radius: 12px; padding: 36px; text-align: center; color: #94a3b8; cursor: pointer; transition: all .2s; margin-bottom: 16px; }
    .upload-zone:hover { border-color: #6366f1; color: #6366f1; background: #f5f3ff; }

    /* USER TABLE */
    .user-table { width: 100%; border-collapse: collapse; font-size: 14px; }
    .user-table th { text-align: left; padding: 10px 14px; font-size: 11px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: .05em; border-bottom: 1px solid #e2e8f0; }
    .user-table td { padding: 12px 14px; border-bottom: 1px solid #f1f5f9; }
    select.role-select { border: 1px solid #e2e8f0; border-radius: 6px; padding: 4px 8px; font-size: 12px; font-weight: 600; background: #f8fafc; }

    /* TOAST */
    .toast { position: fixed; bottom: 24px; right: 24px; background: #1e293b; color: #f1f5f9; padding: 12px 18px; border-radius: 10px; font-size: 13px; font-weight: 500; box-shadow: 0 4px 16px rgba(0,0,0,.2); animation: slideIn .25s ease; z-index: 999; }
    @keyframes slideIn { from { opacity:0; transform: translateY(8px); } to { opacity:1; transform: translateY(0); } }
    .empty { color: #94a3b8; font-size: 14px; padding: 32px; text-align: center; }
  `;

  if (!authUser) {
    return (
      <>
        <style>{css}</style>
        <div className="auth-wrap">
          <div className="auth-card">
            <div className="auth-title">📰 Publish Platform</div>
            <div className="auth-sub">{mode === 'login' ? 'Đăng nhập để tiếp tục' : 'Tạo tài khoản mới'}</div>
            
            {mode === 'login' ? (
              <>
                {loginError && <div className="auth-error">{loginError}</div>}
                <form onSubmit={e => { e.preventDefault(); handleLogin(); }}>
                  <label className="form-label">Username</label>
                  <input className="form-input" value={loginForm.username} onChange={e => setLoginForm(f => ({...f, username: e.target.value}))} placeholder="Nhập username..." />
                  <label className="form-label">Mật khẩu</label>
                  <input className="form-input" type="password" value={loginForm.password} onChange={e => setLoginForm(f => ({...f, password: e.target.value}))} placeholder="Nhập mật khẩu..." />
                  <button className="btn-primary" type="submit" disabled={loginLoading}>{loginLoading ? '⏳ Đang đăng nhập...' : 'Đăng nhập'}</button>
                </form>
                <div style={{ textAlign: 'center', marginTop: 16, fontSize: 13, color: '#64748b' }}>
                  Chưa có tài khoản? <button onClick={toggleMode} style={{ background: 'none', border: 'none', color: '#6366f1', cursor: 'pointer', fontWeight: 600 }}>Đăng ký ngay</button>
                </div>
              </>
            ) : (
              <>
                {regError && <div className="auth-error">{regError}</div>}
                <form onSubmit={e => { e.preventDefault(); handleRegister(); }}>
                  <label className="form-label">Username</label>
                  <input className="form-input" value={regForm.username} onChange={e => setRegForm(f => ({...f, username: e.target.value}))} placeholder="Chọn username..." />
                  <label className="form-label">Email</label>
                  <input className="form-input" type="email" value={regForm.email} onChange={e => setRegForm(f => ({...f, email: e.target.value}))} placeholder="email@example.com" />
                  <label className="form-label">Mật khẩu</label>
                  <input className="form-input" type="password" value={regForm.password} onChange={e => setRegForm(f => ({...f, password: e.target.value}))} placeholder="Tối thiểu 6 ký tự" />
                  <label className="form-label">Xác nhận mật khẩu</label>
                  <input className="form-input" type="password" value={regForm.confirmPassword} onChange={e => setRegForm(f => ({...f, confirmPassword: e.target.value}))} placeholder="Nhập lại mật khẩu" />
                  <button className="btn-primary" type="submit" disabled={regLoading}>{regLoading ? '⏳ Đang tạo tài khoản...' : 'Đăng ký'}</button>
                </form>
                <div style={{ textAlign: 'center', marginTop: 16, fontSize: 13, color: '#64748b' }}>
                  Đã có tài khoản? <button onClick={toggleMode} style={{ background: 'none', border: 'none', color: '#6366f1', cursor: 'pointer', fontWeight: 600 }}>Đăng nhập</button>
                </div>
              </>
            )}
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <style>{css}</style>
      <nav className="nav">
        <span className="nav-brand">📰 Publish Platform</span>
        <button className={`nav-tab ${tab === 'articles' ? 'active' : ''}`} onClick={() => setTab('articles')}>✍️ Bài viết</button>
        {authUser.canManageMedia && <button className={`nav-tab ${tab === 'media' ? 'active' : ''}`} onClick={() => setTab('media')}>🖼️ Media</button>}
        {authUser.canManageUsers && <button className={`nav-tab ${tab === 'users'   ? 'active' : ''}`} onClick={() => setTab('users')}>👥 Phân quyền</button>}
        <span className="nav-user">{authUser.username} · <RoleBadge role={authUser.role} /></span>
        <button className="btn-logout" onClick={handleLogout}>Đăng xuất</button>
      </nav>

      <div className="container page">
        {tab === 'articles' && <ArticlesTab authUser={authUser} />}
        {tab === 'media'    && <MediaTab    authUser={authUser} />}
        {tab === 'users'    && <UsersTab    authUser={authUser} />}
      </div>
    </>
  );
}

// ════════════════════════════════════════════════════════════════════
// ARTICLES TAB
// ════════════════════════════════════════════════════════════════════
function ArticlesTab({ authUser }: { authUser: AuthUser }) {
  const [articles, setArticles]   = useState<Article[]>([]);
  const [editing, setEditing]     = useState<Article | null>(null);
  const [title, setTitle]         = useState('');
  const [content, setContent]     = useState('');
  const [loading, setLoading]     = useState(false);
  const [toast, setToast]         = useState('');

  const showToast = (msg: string) => { setToast(msg); setTimeout(() => setToast(''), 3000); };

  useEffect(() => { loadArticles(); }, []);

  const loadArticles = async () => {
    const data = await api.articles.getMine(authUser.username);
    setArticles(Array.isArray(data) ? data : []);
  };

  const handleSaveDraft = async () => {
    if (!title.trim() || !content.trim()) return showToast('⚠️ Vui lòng điền tiêu đề và nội dung');
    setLoading(true);
    try {
      if (editing) {
        await api.articles.update(editing.id, title, content, authUser.username);
        showToast('✅ Đã cập nhật bài viết');
      } else {
        await api.articles.create(title, content, authUser.username);
        showToast('✅ Đã lưu nháp');
      }
      setTitle(''); setContent(''); setEditing(null);
      loadArticles();
    } catch { showToast('❌ Có lỗi xảy ra'); }
    finally { setLoading(false); }
  };

  const handlePublish = async (id: number) => {
    try {
      await api.articles.publish(id, authUser.username);
      showToast('🚀 Đã đăng bài!');
      loadArticles();
    } catch { showToast('❌ Không có quyền đăng bài'); }
  };

  const handleArchive = async (id: number) => {
    try {
      await api.articles.archive(id, authUser.username);
      showToast('📦 Đã archive');
      loadArticles();
    } catch { showToast('❌ Lỗi archive'); }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Xác nhận xóa bài viết?')) return;
    await api.articles.delete(id, authUser.username);
    showToast('🗑️ Đã xóa');
    loadArticles();
  };

  const startEdit = (a: Article) => { setEditing(a); setTitle(a.title); setContent(a.content); window.scrollTo(0,0); };

  const fmtDate = (d: string) => new Date(d).toLocaleDateString('vi-VN');

  return (
    <div>
      {toast && <div className="toast">{toast}</div>}
      <div className="editor-grid">
        {/* Editor */}
        <div>
          <div className="page-title">{editing ? '✏️ Sửa bài viết' : '✍️ Viết bài mới'}</div>
          <div className="editor-area">
            <input className="form-input" value={title} onChange={e => setTitle(e.target.value)} placeholder="Tiêu đề bài viết..." style={{ fontSize: 16, fontWeight: 600, marginBottom: 0 }} />
            <textarea className="form-textarea" value={content} onChange={e => setContent(e.target.value)} placeholder="Nội dung bài viết..." rows={12} />
            <div className="btn-row">
              <button className="btn btn-blue" onClick={handleSaveDraft} disabled={loading}>{loading ? '⏳...' : editing ? '💾 Cập nhật nháp' : '💾 Lưu nháp'}</button>
              {editing && <button className="btn btn-gray" onClick={() => { setEditing(null); setTitle(''); setContent(''); }}>Hủy</button>}
            </div>
          </div>
        </div>

        {/* Article list */}
        <div>
          <div className="page-title">📋 Bài viết của tôi ({articles.length})</div>
          <div className="card" style={{ padding: '8px 16px' }}>
            {articles.length === 0 && <div className="empty">Chưa có bài viết nào</div>}
            {articles.map(a => (
              <div key={a.id} className="article-item">
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div className="article-title">{a.title}</div>
                  <div className="article-meta">
                    <StatusBadge status={a.status} />
                    <span>{fmtDate(a.createdAt)}</span>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 4, flexShrink: 0 }}>
                  <button className="btn btn-gray" style={{ padding: '4px 10px', fontSize: 12 }} onClick={() => startEdit(a)}>✏️</button>
                  {authUser.canPublish && a.status === 'DRAFT'      && <button className="btn btn-green" style={{ padding: '4px 10px', fontSize: 12 }} onClick={() => handlePublish(a.id)}>🚀</button>}
                  {authUser.canPublish && a.status === 'PUBLISHED'  && <button className="btn btn-gray"  style={{ padding: '4px 10px', fontSize: 12 }} onClick={() => handleArchive(a.id)}>📦</button>}
                  <button className="btn btn-red" style={{ padding: '4px 10px', fontSize: 12 }} onClick={() => handleDelete(a.id)}>🗑️</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// ════════════════════════════════════════════════════════════════════
// MEDIA TAB
// ════════════════════════════════════════════════════════════════════
function MediaTab({ authUser }: { authUser: AuthUser }) {
  const [mediaList, setMediaList] = useState<MediaFile[]>([]);
  const [uploading, setUploading] = useState(false);
  const [toast, setToast]         = useState('');
  const fileRef = useRef<HTMLInputElement>(null);

  const showToast = (msg: string) => { setToast(msg); setTimeout(() => setToast(''), 3000); };

  useEffect(() => { loadMedia(); }, []);

  const loadMedia = async () => {
    const data = await api.media.getAll(authUser.username);
    setMediaList(Array.isArray(data) ? data : []);
  };

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const data = await api.media.upload(file, authUser.username);
      if (data.error) { showToast('❌ ' + data.error); return; }
      showToast('✅ Upload thành công!');
      loadMedia();
    } catch { showToast('❌ Upload thất bại'); }
    finally { setUploading(false); e.target.value = ''; }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Xóa file này?')) return;
    await api.media.delete(id, authUser.username);
    showToast('🗑️ Đã xóa');
    loadMedia();
  };

  const fmtSize = (b: number) => b < 1048576 ? `${(b/1024).toFixed(0)}KB` : `${(b/1048576).toFixed(1)}MB`;

  return (
    <div>
      {toast && <div className="toast">{toast}</div>}
      <div className="page-title">🖼️ Quản lý Media</div>

      <div className="upload-zone" onClick={() => fileRef.current?.click()}>
        {uploading ? '⏳ Đang upload...' : <>📤 Click để upload ảnh/video/file<br /><span style={{ fontSize: 12 }}>Ảnh tối đa 5MB, Video tối đa 100MB</span></>}
      </div>
      <input ref={fileRef} type="file" style={{ display: 'none' }} onChange={handleUpload} accept="image/*,video/*,.pdf,.doc,.docx" />

      {mediaList.length === 0
        ? <div className="empty card">Chưa có file nào</div>
        : <div className="media-grid">
            {mediaList.map(m => (
              <div key={m.id} className="media-item">
                {m.mediaType === 'IMAGE'
                  ? <img className="media-thumb" src={m.url} alt={m.originalName} />
                  : <div className="media-thumb-placeholder">{m.mediaType === 'VIDEO' ? '🎬' : '📄'}</div>}
                <div className="media-info">
                  <div className="media-name" title={m.originalName}>{m.originalName}</div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 4 }}>
                    <span className="media-size">{fmtSize(m.fileSize)}</span>
                    <button className="btn btn-red" style={{ padding: '2px 8px', fontSize: 11 }} onClick={() => handleDelete(m.id)}>🗑️</button>
                  </div>
                </div>
              </div>
            ))}
          </div>}
    </div>
  );
}

// ════════════════════════════════════════════════════════════════════
// USERS TAB (phân quyền - chỉ ADMIN)
// ════════════════════════════════════════════════════════════════════
function UsersTab({ authUser }: { authUser: AuthUser }) {
  const [users, setUsers]         = useState<User[]>([]);
  const [toast, setToast]         = useState('');
  const [regForm, setRegForm]     = useState({ username: '', email: '', password: '', role: 'WRITER' });

  const showToast = (msg: string) => { setToast(msg); setTimeout(() => setToast(''), 3000); };

  useEffect(() => { loadUsers(); }, []);

  const loadUsers = async () => {
    const data = await api.users.all(authUser.username);
    setUsers(Array.isArray(data) ? data : []);
  };

  const handleRoleChange = async (userId: number, newRole: string) => {
    await api.users.updateRole(userId, newRole, authUser.username);
    showToast('✅ Đã cập nhật vai trò');
    loadUsers();
  };

  const handleRegister = async () => {
    if (!regForm.username || !regForm.email || !regForm.password) return showToast('⚠️ Điền đủ thông tin');
    try {
      const data = await api.auth.register(regForm.username, regForm.email, regForm.password, regForm.role);
      if (data.error) { showToast('❌ ' + data.error); return; }
      showToast('✅ Đã tạo tài khoản: ' + data.username);
      setRegForm({ username: '', email: '', password: '', role: 'WRITER' });
      loadUsers();
    } catch { showToast('❌ Lỗi tạo tài khoản'); }
  };

  return (
    <div>
      {toast && <div className="toast">{toast}</div>}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 20 }}>
        {/* User list */}
        <div>
          <div className="page-title">👥 Danh sách người dùng</div>
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <table className="user-table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Vai trò</th>
                  <th>Thay đổi</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id}>
                    <td style={{ fontWeight: 600 }}>{u.username}</td>
                    <td style={{ color: '#64748b', fontSize: 13 }}>{u.email}</td>
                    <td><RoleBadge role={u.role} /></td>
                    <td>
                      {u.username !== authUser.username && (
                        <select className="role-select" value={u.role} onChange={e => handleRoleChange(u.id, e.target.value)}>
                          <option value="WRITER">WRITER</option>
                          <option value="EDITOR">EDITOR</option>
                          <option value="ADMIN">ADMIN</option>
                        </select>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Create user */}
        <div>
          <div className="page-title">➕ Tạo tài khoản mới</div>
          <div className="card">
            <label className="form-label">Username</label>
            <input className="form-input" value={regForm.username} onChange={e => setRegForm(f => ({...f, username: e.target.value}))} placeholder="username..." />
            <label className="form-label">Email</label>
            <input className="form-input" value={regForm.email} onChange={e => setRegForm(f => ({...f, email: e.target.value}))} placeholder="email@example.com" />
            <label className="form-label">Mật khẩu</label>
            <input className="form-input" type="password" value={regForm.password} onChange={e => setRegForm(f => ({...f, password: e.target.value}))} placeholder="••••••" />
            <label className="form-label">Vai trò</label>
            <select className="form-input role-select" value={regForm.role} onChange={e => setRegForm(f => ({...f, role: e.target.value}))}>
              <option value="WRITER">WRITER — Viết bài</option>
              <option value="EDITOR">EDITOR — Duyệt bài + Media</option>
              <option value="ADMIN">ADMIN — Toàn quyền</option>
            </select>
            <button className="btn btn-blue" style={{ width: '100%' }} onClick={handleRegister}>Tạo tài khoản</button>
          </div>
        </div>
      </div>
    </div>
  );
}
