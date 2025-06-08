document.addEventListener('DOMContentLoaded', function () {
    const togglePassword = document.getElementById('togglePassword');
    const passwordField = document.getElementById('password');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPasswordField = document.getElementById('confirmPassword');

    togglePassword.addEventListener('click', function () {
        const type = passwordField.type === 'password' ? 'text' : 'password';
        passwordField.type = type;
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });

    toggleConfirmPassword.addEventListener('click', function () {
        const type = confirmPasswordField.type === 'password' ? 'text' : 'password';
        confirmPasswordField.type = type;
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });

    const passwordInput = document.getElementById('password');
    const strengthBar = document.getElementById('strengthBar');
    const lengthHint = document.getElementById('lengthHint');
    const numberHint = document.getElementById('numberHint');
    const specialHint = document.getElementById('specialHint');

    passwordInput.addEventListener('input', function () {
        const value = passwordInput.value;
        const hasLength = value.length >= 8;
        const hasNumber = /\d/.test(value);
        const hasSpecial = /[^A-Za-z0-9]/.test(value);

        let strength = 0;
        if (hasLength) strength++;
        if (hasNumber) strength++;
        if (hasSpecial) strength++;

        strengthBar.style.width = `${(strength / 3) * 100}%`;
        strengthBar.style.backgroundColor = strength === 3 ? '#27ae60' : strength === 2 ? '#f39c12' : '#e74c3c';

        toggleHint(lengthHint, hasLength);
        toggleHint(numberHint, hasNumber);
        toggleHint(specialHint, hasSpecial);
    });

    function toggleHint(hintElement, condition) {
        hintElement.classList.toggle('valid', condition);
        hintElement.querySelector('i').classList.toggle('fa-check-circle', condition);
        hintElement.querySelector('i').classList.toggle('fa-circle', !condition);
    }

    const form = document.getElementById('registrationForm');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    form.addEventListener('submit', function (e) {
        if (passwordField.value !== confirmPasswordField.value) {
            e.preventDefault();
            confirmPasswordError.style.display = 'block';
            confirmPasswordField.classList.add('input-error');
        } else {
            confirmPasswordError.style.display = 'none';
            confirmPasswordField.classList.remove('input-error');
        }
    });
});
