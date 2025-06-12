// deselectable-radios.js
(function() {
    // Seleziono il container che racchiude tutti i radio + label
    const container = document.querySelector('.btn-group.mb-3');
    if (!container) return;

    container.addEventListener('click', function(e) {
        // Rilevo se l'elemento cliccato (o uno dei suoi avi) è un <label> in .deselectable-radios
        const lbl = e.target.closest('.deselectable-radios label');
        if (!lbl) return;

        // Recupero l'input via htmlFor
        const radio = document.getElementById(lbl.htmlFor);
        if (!radio) return;

        // Se era già selezionato, blocco il comportamento nativo e lo deseleziono
        if (radio.checked) {
            e.preventDefault();             // blocca la (ri-)selezione
            radio.checked = false;          // deseleziona
            lbl.classList.remove('active'); // rimuove la classe .active di Bootstrap
            // Emetto l’evento change nel caso tu lo stia ascoltando
            radio.dispatchEvent(new Event('change', { bubbles: true }));
        }
        // Se non era selezionato, il radio verrà scelto normalmente
    });
})();
