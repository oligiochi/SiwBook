const adminFab = document.getElementById('adminFab');
const adminMainBtn = document.querySelector('.admin-main-btn');

if (adminFab && adminMainBtn) {
    adminMainBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        adminFab.classList.toggle('active');
    });

    document.addEventListener('click', (e) => {
        if (!adminFab.contains(e.target)) {
            adminFab.classList.remove('active');
        }
    });
}
