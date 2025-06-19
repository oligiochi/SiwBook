document.addEventListener('DOMContentLoaded', function() {
    const titleInput = document.getElementById('reviewTitle');
    const textInput = document.getElementById('reviewText');
    const titleCount = document.getElementById('titleCount');
    const textCount = document.getElementById('textCount');

    titleInput.addEventListener('input', () => {
        const length = titleInput.value.length;
        titleCount.textContent = `${length}/80 caratteri`;
        titleCount.className = 'character-count';
        if (length > 70) titleCount.classList.add('warning');
        if (length > 80) titleCount.classList.add('error');
    });

    textInput.addEventListener('input', () => {
        const length = textInput.value.length;
        textCount.textContent = `${length}/500 caratteri`;
        textCount.className = 'character-count';
        if (length > 450) textCount.classList.add('warning');
        if (length > 500) textCount.classList.add('error');
    });

    const stars = document.querySelectorAll('.star');
    const ratingValue = document.getElementById('ratingValue');

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = parseInt(this.dataset.value);
            ratingValue.value = value;
            stars.forEach((s, i) => s.classList.toggle('active', i < value));
        });

        star.addEventListener('mouseover', function() {
            const value = parseInt(this.dataset.value);
            stars.forEach((s, i) => s.classList.toggle('hover', i < value));
        });

        star.addEventListener('mouseout', () => {
            stars.forEach(s => s.classList.remove('hover'));
        });
    });

    const form = document.getElementById('reviewForm');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');


    form.addEventListener('reset', function() {
        stars.forEach(s => s.classList.remove('active'));
        ratingValue.value = 0;
        titleInput.classList.remove('input-error');
        textInput.classList.remove('input-error');
        errorMessage.style.display = 'none';
        successMessage.style.display = 'none';
        titleCount.textContent = "0/80 caratteri";
        textCount.textContent = "0/500 caratteri";
    });
});
