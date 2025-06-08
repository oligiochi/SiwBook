document.addEventListener('DOMContentLoaded', function () {
    const togglePwd = document.getElementById('togglePwd');
    const pwdField = document.getElementById('pwdField');

    togglePwd.addEventListener('click', function () {
        const type = pwdField.getAttribute('type') === 'password' ? 'text' : 'password';
        pwdField.setAttribute('type', type);
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });
});
