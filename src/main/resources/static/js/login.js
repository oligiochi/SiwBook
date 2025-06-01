document.addEventListener('DOMContentLoaded', function() {
    const pwdField = document.getElementById('pwdField');
    const toggleIcon = document.getElementById('togglePwd');
    let visible = false;

    toggleIcon.addEventListener('click', function() {
        visible = !visible;
        if (visible) {
            pwdField.setAttribute('type', 'text');
            toggleIcon.classList.remove('fa-eye');
            toggleIcon.classList.add('fa-eye-slash');
        } else {
            pwdField.setAttribute('type', 'password');
            toggleIcon.classList.remove('fa-eye-slash');
            toggleIcon.classList.add('fa-eye');
        }
    });
});
