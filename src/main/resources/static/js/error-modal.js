document.addEventListener('DOMContentLoaded', function () {
    const hasError = document.body.getAttribute('data-has-error');

    if (hasError === 'true') {
        const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
        errorModal.show();
    }
});
