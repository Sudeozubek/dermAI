// Blog postlarını ve yorumları localStorage'da tutan, paylaşım, yorum ve yanıt eklemeyi sağlayan sistem

// Blog postlarını yükle ve ekrana bas
function loadPosts() {
    const postsList = document.getElementById('postsList');
    postsList.innerHTML = '';
    let posts = JSON.parse(localStorage.getItem('blogPosts') || '[]');
    posts.reverse().forEach((post, postIdx) => {
        const postDiv = document.createElement('div');
        postDiv.className = 'blog-post';
        postDiv.innerHTML = `
            <div class="post-header">
                <img src="${post.icon}" alt="Profil" class="profile-icon">
                <div class="post-user-info">
                    <span class="post-author">${post.author}</span>
                    <span class="post-date">${post.date}</span>
                </div>
            </div>
            <div class="post-content">
                <h3>${post.title}</h3>
                <p>${post.content}</p>
            </div>
            <div class="post-actions">
                <button class="like-btn" data-post="${postIdx}">❤️ ${post.likes || 0}</button>
                <button class="reply-btn" data-post="${postIdx}">Yanıtla</button>
            </div>
            <div class="post-comments" id="comments-${postIdx}">
                ${post.comments.map((comment, cIdx) => `
                    <div class="comment">
                        <img src="${comment.icon}" alt="Profil" class="profile-icon comment-icon">
                        <div class="comment-body">
                            <span class="comment-author">${comment.author}</span>
                            <span class="comment-date">${comment.date}</span>
                            <p>${comment.content}</p>
                            <button class="like-btn small" data-post="${postIdx}" data-comment="${cIdx}">❤️ ${comment.likes || 0}</button>
                            <button class="reply-btn small" data-post="${postIdx}" data-comment="${cIdx}">Yanıtla</button>
                        </div>
                        ${comment.replies && comment.replies.length > 0 ? `
                            <div class="comment-replies">
                                ${comment.replies.map(reply => `
                                    <div class="comment reply">
                                        <img src="${reply.icon}" alt="Profil" class="profile-icon comment-icon">
                                        <div class="comment-body">
                                            <span class="comment-author">${reply.author}</span>
                                            <span class="comment-date">${reply.date}</span>
                                            <p>${reply.content}</p>
                                        </div>
                                    </div>
                                `).join('')}
                            </div>
                        ` : ''}
                    </div>
                    ${comment.replying ? `<form class="comment-form" data-post="${postIdx}" data-parent="${cIdx}">
                        <img src="${getCurrentUserIcon()}" alt="Profil" class="profile-icon comment-icon">
                        <input type="text" placeholder="Yanıtını yaz..." required>
                        <button type="submit">Gönder</button>
                    </form>` : ''}
                `).join('')}
                <form class="comment-form" data-post="${postIdx}">
                    <img src="${getCurrentUserIcon()}" alt="Profil" class="profile-icon comment-icon">
                    <input type="text" placeholder="Yorumunu yaz..." required>
                    <button type="submit">Gönder</button>
                </form>
            </div>
        `;
        postsList.appendChild(postDiv);
    });
}

function getCurrentUser() {
    return localStorage.getItem('username') || 'Kullanıcı';
}
function getCurrentUserIcon() {
    return localStorage.getItem('profileIcon') || 'icon1.png';
}
function getToday() {
    const d = new Date();
    return d.toLocaleDateString('tr-TR', { day: '2-digit', month: 'long', year: 'numeric' });
}

// Paylaşım ekleme
if(document.getElementById('blogForm')) {
    document.getElementById('blogForm').addEventListener('submit', function(e) {
        e.preventDefault();
        let posts = JSON.parse(localStorage.getItem('blogPosts') || '[]');
        const post = {
            author: getCurrentUser(),
            icon: getCurrentUserIcon(),
            date: getToday(),
            title: document.getElementById('postTitle').value,
            content: document.getElementById('postContent').value,
            likes: 0,
            comments: []
        };
        posts.push(post);
        localStorage.setItem('blogPosts', JSON.stringify(posts));
        document.getElementById('blogForm').reset();
        loadPosts();
    });
}

// Yorum ve yanıt ekleme, beğenme, yanıtla butonu
if(document.getElementById('postsList')) {
    document.getElementById('postsList').addEventListener('submit', function(e) {
        if(e.target.classList.contains('comment-form')) {
            e.preventDefault();
            let posts = JSON.parse(localStorage.getItem('blogPosts') || '[]');
            const postIdx = e.target.getAttribute('data-post');
            const parentIdx = e.target.getAttribute('data-parent');
            const comment = {
                author: getCurrentUser(),
                icon: getCurrentUserIcon(),
                date: getToday(),
                content: e.target.querySelector('input').value,
                likes: 0,
                replies: []
            };
            if(parentIdx !== null) {
                // Yanıt olarak ekle (yorumun replies dizisine)
                if(!posts[postIdx].comments[parentIdx].replies) posts[postIdx].comments[parentIdx].replies = [];
                posts[postIdx].comments[parentIdx].replies.push({
                    author: getCurrentUser(),
                    icon: getCurrentUserIcon(),
                    date: getToday(),
                    content: e.target.querySelector('input').value
                });
                posts[postIdx].comments[parentIdx].replying = false;
            } else {
                posts[postIdx].comments.push(comment);
            }
            localStorage.setItem('blogPosts', JSON.stringify(posts));
            loadPosts();
        }
    });
    document.getElementById('postsList').addEventListener('click', function(e) {
        let posts = JSON.parse(localStorage.getItem('blogPosts') || '[]');
        // Post beğenme
        if(e.target.classList.contains('like-btn') && e.target.hasAttribute('data-post') && !e.target.hasAttribute('data-comment')) {
            const postIdx = e.target.getAttribute('data-post');
            posts[postIdx].likes = (posts[postIdx].likes || 0) + 1;
            localStorage.setItem('blogPosts', JSON.stringify(posts));
            loadPosts();
        }
        // Yorum beğenme
        if(e.target.classList.contains('like-btn') && e.target.hasAttribute('data-post') && e.target.hasAttribute('data-comment')) {
            const postIdx = e.target.getAttribute('data-post');
            const cIdx = e.target.getAttribute('data-comment');
            posts[postIdx].comments[cIdx].likes = (posts[postIdx].comments[cIdx].likes || 0) + 1;
            localStorage.setItem('blogPosts', JSON.stringify(posts));
            loadPosts();
        }
        // Yanıtla butonu (yorum için)
        if(e.target.classList.contains('reply-btn') && e.target.hasAttribute('data-post') && e.target.hasAttribute('data-comment')) {
            const postIdx = e.target.getAttribute('data-post');
            const cIdx = e.target.getAttribute('data-comment');
            posts[postIdx].comments.forEach(c => c.replying = false);
            posts[postIdx].comments[cIdx].replying = true;
            localStorage.setItem('blogPosts', JSON.stringify(posts));
            loadPosts();
            // Yorum kutusuna odaklan
            setTimeout(() => {
                const form = document.querySelector(`form.comment-form[data-post='${postIdx}'][data-parent='${cIdx}'] input`);
                if(form) form.focus();
            }, 100);
        }
        // Yanıtla butonu (post için, şimdilik sadece yorum kutusuna odaklanıyor)
        if(e.target.classList.contains('reply-btn') && e.target.hasAttribute('data-post') && !e.target.hasAttribute('data-comment')) {
            const postIdx = e.target.getAttribute('data-post');
            setTimeout(() => {
                const form = document.querySelector(`form.comment-form[data-post='${postIdx}']:not([data-parent]) input`);
                if(form) form.focus();
            }, 100);
        }
    });
}

// Sayfa yüklendiğinde postları yükle
if(document.getElementById('postsList')) {
    loadPosts();
}
