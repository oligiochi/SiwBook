// deselectable-radios-dynamic.js
(function() {
    document.addEventListener('mousedown', function(e) {
        const radio = e.target.closest('.deselectable-radios input[type="radio"]');
        if (radio) {
            // Salvo lo stato *prima* del click
            radio.dataset.wasChecked = radio.checked;
        }
    });

    document.addEventListener('click', function(e) {
        const radio = e.target.closest('.deselectable-radios input[type="radio"]');
        if (!radio) return;

        // Se era già checked al mousedown, deseleziono
        if (radio.dataset.wasChecked === 'true') {
            e.preventDefault();
            radio.checked = false;

            // Rimuovo la classe .active sul label di Bootstrap
            const lbl = document.querySelector(`label[for="${radio.id}"]`);
            if (lbl) lbl.classList.remove('active');

            // Emetto change se ti serve intercettarlo
            radio.dispatchEvent(new Event('change', { bubbles: true }));
        }
    });
})();
