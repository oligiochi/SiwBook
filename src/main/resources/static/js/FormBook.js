document.addEventListener('DOMContentLoaded', function() {
    // === 1) Elementi DOM comuni ===
    const bookForm       = document.getElementById('bookForm');
    const bookId         = bookForm.dataset.bookId || null;
    const titleInput     = document.getElementById('bookTitle');
    const titleCount     = document.getElementById('titleCount');
    const authorDropdown = document.getElementById('authorDropdown');
    const genreDropdown  = document.getElementById('genreDropdown');

    // === 2) Elementi gestione immagini ===
    const imageUpload    = document.getElementById('imageUpload');
    const uploadTrigger  = document.getElementById('uploadTrigger');
    const dropArea       = document.getElementById('dropArea');
    const imagePreview   = document.getElementById('imagePreview');
    let uploadedImages   = [];

    // === 3) Caricamento immagini esistenti (modalità modifica) ===
    if (bookId) loadExistingImages();
    else {
        const existingIds = JSON.parse(imagePreview.getAttribute('data-existing-image-ids') || '[]');
        uploadedImages = existingIds.map(id => ({ id, url: `/images/${id}`, originalFileName: `Immagine ${id}` }));
        renderImages();
    }

    // === 4) Eventi immagine: upload e drag’n’drop ===
    uploadTrigger.addEventListener('click', () => imageUpload.click());
    imageUpload.addEventListener('change', e => uploadFiles(Array.from(e.target.files)));

    ['dragenter','dragover','dragleave','drop'].forEach(evt =>
        dropArea.addEventListener(evt, e => { e.preventDefault(); e.stopPropagation(); })
    );
    ['dragenter','dragover'].forEach(evt =>
        dropArea.addEventListener(evt, () => dropArea.classList.add('highlight'))
    );
    ['dragleave','drop'].forEach(evt =>
        dropArea.addEventListener(evt, () => dropArea.classList.remove('highlight'))
    );
    dropArea.addEventListener('drop', e => uploadFiles(Array.from(e.dataTransfer.files)));

    async function uploadFiles(files) {
        if (!files.length) return;
        const formData = new FormData();
        files.forEach(f => formData.append('files', f));
        if (bookId) formData.append('bookId', bookId);

        try {
            showLoadingState();
            const res = await fetch('/images/upload', { method: 'POST', body: formData });
            const data = await res.json();
            if (data.success && data.images) {
                uploadedImages.push(...data.images);
                renderImages();
            } else {
                showError(data.message || 'Errore nel caricamento immagini');
            }
        } catch (err) {
            console.error(err);
            showError('Errore durante l’upload');
        } finally {
            hideLoadingState();
        }
    }

    async function loadExistingImages() {
        try {
            const res = await fetch(`/images/book/${bookId}/images`);
            const images = await res.json();
            uploadedImages = images;
            renderImages();
        } catch (err) {
            console.error('Errore caricamento immagini:', err);
        }
    }

    async function deleteImage(imageId) {
        if (!confirm('Sei sicuro di voler eliminare questa immagine?')) return;
        try {
            const res = await fetch(`/images/${imageId}`, { method: 'DELETE' });
            if (res.ok) {
                uploadedImages = uploadedImages.filter(img => img.id !== imageId);
                renderImages();
            } else {
                showError('Impossibile eliminare l’immagine');
            }
        } catch (err) {
            showError('Errore durante l’eliminazione');
        }
    }

    function renderImages() {
        imagePreview.innerHTML = '';
        uploadedImages.forEach(img => {
            const container = document.createElement('div');
            container.className = 'image-actions';
            container.innerHTML = `
                <img class="image-preview" src="${img.url}" alt="${img.originalFileName}">
                <div class="remove-image" data-image-id="${img.id}">&times;</div>
            `;
            container.querySelector('.remove-image').addEventListener('click', () => deleteImage(img.id));
            imagePreview.appendChild(container);
        });
        updateHiddenImageIds();
    }

    function updateHiddenImageIds() {
        let hidden = document.getElementById('imageIds');
        if (!hidden) {
            hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = 'imageIds';
            hidden.id = 'imageIds';
            bookForm.appendChild(hidden);
        }
        hidden.value = uploadedImages.map(img => img.id).join(',');
    }

    function showLoadingState() {
        uploadTrigger.disabled = true;
        uploadTrigger.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Caricamento...';
    }

    function hideLoadingState() {
        uploadTrigger.disabled = false;
        uploadTrigger.innerHTML = '<i class="fas fa-cloud-upload-alt"></i> Scegli Immagini';
    }

    function showError(msg) {
        alert(msg);
    }

    // === 5) Contatore caratteri titolo ===
    titleInput.addEventListener('input', () => {
        titleCount.textContent = titleInput.value.length;
    });

    // === 6) Multi-select autori e generi ===
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

    // === 7) Reset ===
    bookForm.addEventListener('reset', () => {
        setTimeout(() => {
            document.getElementById('selectedAuthors').innerHTML = '';
            document.getElementById('selectedGenres').innerHTML  = '';
            document.getElementById('authorsField').value       = '';
            document.getElementById('genresField').value        = '';
            uploadedImages = [];
            renderImages();
            titleCount.textContent = '0';
        }, 0);
    });
});
