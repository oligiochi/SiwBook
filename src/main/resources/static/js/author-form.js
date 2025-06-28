document.addEventListener('DOMContentLoaded', function () {
    // === 1) ELEMENTI DOM ===
    const authorForm = document.getElementById('authorForm');
    const bookInput = document.getElementById('bookInput');
    const bookDropdown = document.getElementById('bookDropdown');
    const selectedBooks = document.getElementById('selectedBooks');
    const booksField = document.getElementById('booksField');

    const imageUpload = document.getElementById('imageUpload');
    const uploadTrigger = document.getElementById('uploadTrigger');
    const imagePreview = document.getElementById('imagePreview');
    const imageError = document.getElementById('image-error');
    let selectedImage = null;

    // === 2) MULTISELECT LIBRI ===
    setupMultiSelect(bookInput, bookDropdown, selectedBooks, booksField);

    // === 3) UPLOAD IMMAGINE ===
    uploadTrigger.addEventListener('click', () => imageUpload.click());
    imageUpload.addEventListener('change', handleImageUpload);

    function handleImageUpload(e) {
        const file = e.target.files[0];
        if (file && file.type.match('image.*')) {
            processImage(file);
        } else {
            showImageError('Per favore seleziona un file immagine valido');
        }
    }

    function processImage(file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            imagePreview.innerHTML = '';

            const img = document.createElement('img');
            img.className = 'image-preview';
            img.src = e.target.result;

            const removeBtn = document.createElement('div');
            removeBtn.className = 'remove-image';
            removeBtn.innerHTML = '&times;';
            removeBtn.onclick = function () {
                imagePreview.innerHTML = '';
                imageUpload.value = '';
                selectedImage = null;
            };

            const container = document.createElement('div');
            container.className = 'image-actions';
            container.appendChild(img);
            container.appendChild(removeBtn);
            imagePreview.appendChild(container);

            selectedImage = file;
        };
        reader.readAsDataURL(file);
    }

    function showImageError(message) {
        imageError.textContent = message;
        setTimeout(() => {
            imageError.textContent = '';
        }, 5000);
    }

    // === 4) VALIDAZIONE E SUBMIT ===
    // Rimosso: il submit lascia fare al backend la validazione e il redirect.

    // === 5) FUNZIONE MULTISELECT ===
    function setupMultiSelect(inputEl, dropdownEl, selectedEl, hiddenField) {
        const items = dropdownEl.querySelectorAll('.dropdown-item');
        const selectedIds = [];

        if (hiddenField.value) {
            hiddenField.value.split(',').map(s => s.trim()).filter(s => s).forEach(id => {
                const item = dropdownEl.querySelector(`.dropdown-item[data-id="${id}"]`);
                if (item) addItem(id, item.textContent);
                selectedIds.push(id);
            });
            updateHidden();
            filterItems();
        }

        inputEl.addEventListener('focus', () => dropdownEl.style.display = 'block');
        inputEl.addEventListener('blur', () => setTimeout(() => dropdownEl.style.display = 'none', 200));
        inputEl.addEventListener('input', filterItems);

        items.forEach(i => i.addEventListener('click', () => {
            const id = i.dataset.id;
            if (!selectedIds.includes(id)) {
                addItem(id, i.textContent);
                selectedIds.push(id);
                updateHidden();
            }
            inputEl.value = '';
            filterItems();
        }));

        function filterItems() {
            const term = inputEl.value.toLowerCase();
            items.forEach(i => {
                const already = selectedIds.includes(i.dataset.id);
                const match = i.textContent.toLowerCase().includes(term);
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
                updateHidden();
                filterItems();
            });
            selectedEl.appendChild(el);
        }

        function updateHidden() {
            hiddenField.value = selectedIds.join(',');
        }
    }
});
