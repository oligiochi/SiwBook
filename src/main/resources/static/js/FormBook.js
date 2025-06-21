document.addEventListener('DOMContentLoaded', function() {

    let imageFiles = [];

    // Contatore caratteri titolo
    const titleInput = document.getElementById('bookTitle');
    const titleCount = document.getElementById('titleCount');
    titleInput.addEventListener('input', () => {
        titleCount.textContent = titleInput.value.length;
    });

    // Seleziona dropdown autori e generi (popolati da Thymeleaf)
    const authorDropdown = document.getElementById('authorDropdown');
    const genreDropdown = document.getElementById('genreDropdown');

    // Funzione per multi-select (autori/generi)
    function setupMultiSelect(inputEl, dropdownEl, selectedEl) {
        const items = dropdownEl.querySelectorAll('.dropdown-item');
        inputEl.addEventListener('focus', () => dropdownEl.style.display = 'block');
        inputEl.addEventListener('blur', () => setTimeout(() => dropdownEl.style.display = 'none', 200));
        inputEl.addEventListener('input', filterItems);
        items.forEach(i => i.addEventListener('click', () => {
            addItem(i.dataset.id, i.textContent);
            inputEl.value = '';
            filterItems();
        }));

        function filterItems() {
            const term = inputEl.value.toLowerCase();
            const selIds = Array.from(selectedEl.children).map(c => c.dataset.id);
            items.forEach(i => {
                const match = i.textContent.toLowerCase().includes(term) && !selIds.includes(i.dataset.id);
                i.style.display = match ? 'block' : 'none';
            });
        }
        function addItem(id, name) {
            if (!Array.from(selectedEl.children).some(c => c.dataset.id === id)) {
                const el = document.createElement('div');
                el.className = 'selected-item';
                el.dataset.id = id;
                el.innerHTML = `${name}<button class="remove-item">&times;</button>`;
                el.querySelector('.remove-item').addEventListener('click', () => {
                    el.remove();
                    filterItems();
                });
                selectedEl.appendChild(el);
            }
        }
    }

    // Inizializza i multi-select
    setupMultiSelect(
        document.getElementById('authorInput'),
        authorDropdown,
        document.getElementById('selectedAuthors')
    );
    setupMultiSelect(
        document.getElementById('genreInput'),
        genreDropdown,
        document.getElementById('selectedGenres')
    );

    // Upload immagini e drag’n’drop
    const uploadTrigger = document.getElementById('uploadTrigger');
    const imageUpload = document.getElementById('imageUpload');
    const imagePreview = document.getElementById('imagePreview');
    const dropArea = document.getElementById('dropArea');

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

    function renderPreviews() {
        imagePreview.innerHTML = '';
        imageFiles.forEach((file, idx) => {
            const reader = new FileReader();
            reader.onload = ev => {
                const c = document.createElement('div');
                c.className = 'image-actions';
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
            reader.readAsDataURL(file);
        });
    }

    // Gestione form, reset e successo
    const bookForm = document.getElementById('bookForm');
    const successMessage = document.getElementById('successMessage');
    bookForm.addEventListener('reset', () => {
        document.getElementById('selectedAuthors').innerHTML = '';
        document.getElementById('selectedGenres').innerHTML = '';
        imagePreview.innerHTML = '';
        imageFiles = [];
        titleCount.textContent = '0';
    });
    bookForm.addEventListener('submit', e => {
        e.preventDefault();
        successMessage.style.display = 'block';
        setTimeout(() => {
            successMessage.style.display = 'none';
            bookForm.reset();
        }, 3000);
    });

    // Imposta oggi come default
    document.getElementById('releaseDate').value =
        new Date().toISOString().split('T')[0];
});
