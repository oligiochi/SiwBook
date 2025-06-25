// FormBook.js

document.addEventListener('DOMContentLoaded', function() {
    // 1) Recupera gli ID esistenti dal data-attribute di #imagePreview
    const imagePreview = document.getElementById('imagePreview');
    const existingIds = JSON.parse(
        imagePreview.getAttribute('data-existing-image-ids') || '[]'
    );
    const existingImages = existingIds.map(id => ({
        id,
        url: `/images/${id}`
    }));

    // 2) Inizializza imageFiles con le esistenti
    let imageFiles = existingImages.slice();  // elementi {id, url} e poi File

    // 3) Elementi del DOM
    const titleInput    = document.getElementById('bookTitle');
    const titleCount    = document.getElementById('titleCount');
    const authorDropdown= document.getElementById('authorDropdown');
    const genreDropdown = document.getElementById('genreDropdown');
    const imageUpload   = document.getElementById('imageUpload');
    const uploadTrigger = document.getElementById('uploadTrigger');
    const dropArea      = document.getElementById('dropArea');
    const bookForm      = document.getElementById('bookForm');

    // 4) Contatore caratteri titolo
    titleInput.addEventListener('input', () => {
        titleCount.textContent = titleInput.value.length;
    });

    // 5) Multi-select autori e generi (come prima)
    setupMultiSelect(
        document.getElementById('authorInput'),
        authorDropdown,
        document.getElementById('selectedAuthors'),
        document.getElementById('authorsField')
    );
    setupMultiSelect(
        document.getElementById('genreInput'),
        genreDropdown,
        document.getElementById('selectedGenres'),
        document.getElementById('genresField')
    );

    function setupMultiSelect(inputEl, dropdownEl, selectedEl, hiddenField) {
        const items = dropdownEl.querySelectorAll('.dropdown-item');
        const selectedIds = [];

        if (hiddenField.value) {
            hiddenField.value.split(',').map(s=>s.trim()).filter(s=>s).forEach(id => {
                const item = dropdownEl.querySelector(`.dropdown-item[data-id="${id}"]`);
                if (item) addItem(id, item.textContent);
                selectedIds.push(id);
            });
            updateHidden(); filterItems();
        }

        inputEl.addEventListener('focus',  () => dropdownEl.style.display = 'block');
        inputEl.addEventListener('blur',   () => setTimeout(() => dropdownEl.style.display = 'none', 200));
        inputEl.addEventListener('input',  filterItems);

        items.forEach(i => i.addEventListener('click', () => {
            const id = i.dataset.id;
            if (!selectedIds.includes(id)) {
                addItem(id, i.textContent);
                selectedIds.push(id);
                updateHidden();
            }
            inputEl.value = ''; filterItems();
        }));

        function filterItems() {
            const term = inputEl.value.toLowerCase();
            items.forEach(i => {
                const already = selectedIds.includes(i.dataset.id);
                const match   = i.textContent.toLowerCase().includes(term);
                i.style.display = (!already && match) ? 'block' : 'none';
            });
        }
        function addItem(id, name) {
            const el = document.createElement('div');
            el.className = 'selected-item';
            el.dataset.id = id;
            el.innerHTML = `${name} <button class="remove-item">&times;</button>`;
            el.querySelector('.remove-item').addEventListener('click', () => {
                el.remove();
                const idx = selectedIds.indexOf(id);
                if (idx > -1) selectedIds.splice(idx, 1);
                updateHidden(); filterItems();
            });
            selectedEl.appendChild(el);
        }
        function updateHidden() {
            hiddenField.value = selectedIds.join(',');
        }
    }

    // 6) Caricamento nuove immagini + drag’n’drop
    uploadTrigger.addEventListener('click', () => imageUpload.click());
    imageUpload.addEventListener('change', e => {
        imageFiles.push(...e.target.files);
        renderPreviews();
    });
    ['dragenter','dragover','dragleave','drop'].forEach(evt =>
        dropArea.addEventListener(evt, e => { e.preventDefault(); e.stopPropagation(); })
    );
    ['dragenter','dragover'].forEach(evt =>
        dropArea.addEventListener(evt, () => dropArea.classList.add('highlight'))
    );
    ['dragleave','drop'].forEach(evt =>
        dropArea.addEventListener(evt, () => dropArea.classList.remove('highlight'))
    );
    dropArea.addEventListener('drop', e => {
        imageFiles.push(...e.dataTransfer.files);
        renderPreviews();
    });

    // 7) renderPreviews unificato, con STESSA grafica
    function renderPreviews() {
        imagePreview.innerHTML = '';

        imageFiles.forEach((entry, idx) => {
            // crea wrapper identico a prima
            const c = document.createElement('div');
            c.className = 'image-actions';

            if (entry instanceof File) {
                // Nuova immagine: come prima
                const reader = new FileReader();
                reader.onload = ev => {
                    c.innerHTML = `
            <img class="image-preview" src="${ev.target.result}" data-index="${idx}">
            <div class="remove-image" data-index="${idx}">&times;</div>
          `;
                    c.querySelector('.remove-image').addEventListener('click', () => {
                        imageFiles.splice(idx, 1);
                        renderPreviews();
                    });
                    imagePreview.appendChild(c);
                };
                reader.readAsDataURL(entry);

            } else {
                // Immagine esistente: stessa grafica
                c.innerHTML = `
          <img class="image-preview" src="${entry.url}" data-id="${entry.id}">
          <div class="remove-image" data-id="${entry.id}">&times;</div>
        `;
                c.querySelector('.remove-image').addEventListener('click', () => {
                    imageFiles = imageFiles.filter(e => e.id !== entry.id);
                    renderPreviews();
                    // segnala al server di rimuovere
                    const hidden = document.createElement('input');
                    hidden.type  = 'hidden';
                    hidden.name  = 'removeImageIds';
                    hidden.value = entry.id;
                    bookForm.appendChild(hidden);
                });
                imagePreview.appendChild(c);
            }
        });
    }

    // 8) Reset del form
    bookForm.addEventListener('reset', () => {
        setTimeout(() => {
            document.getElementById('selectedAuthors').innerHTML = '';
            document.getElementById('selectedGenres').innerHTML  = '';
            document.getElementById('authorsField').value       = '';
            document.getElementById('genresField').value        = '';
            imageFiles = existingImages.slice();
            titleCount.textContent = '0';
            renderPreviews();
        }, 0);
    });

    // 9) Submit: solo File reali
    bookForm.addEventListener('submit', () => {
        const dt = new DataTransfer();
        imageFiles.forEach(e => {
            if (e instanceof File) dt.items.add(e);
        });
        imageUpload.files = dt.files;
    });

    // 10) Primo render
    renderPreviews();
});
