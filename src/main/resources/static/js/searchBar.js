document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('searchForm');
    const selectedGenreInput = document.getElementById('selectedGenre');

    // Gestione click sulle categorie
    document.querySelectorAll('.tags-container .tag').forEach(button => {
        button.addEventListener('click', function() {
            const genreId = this.getAttribute('data-genre-id');

            // Aggiorna il valore del campo nascosto
            selectedGenreInput.value = genreId;

            // Invia il form
            form.submit();
        });
    });

    // Resetta il genere quando si fa una nuova ricerca testuale
    document.querySelector('.search-input').addEventListener('input', function(e) {
        if(e.target.value.trim() === '') {
            selectedGenreInput.value = '';
        }
    });
});