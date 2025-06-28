// Lista "books" viene passata dal template Thymeleaf
let selectedBooks = [];
let selectedImage = null;

document.addEventListener('DOMContentLoaded', function() {
    populateBookDropdown();
    document.getElementById('bookInput').addEventListener('input', filterBooks);
    document.getElementById('bookInput').addEventListener('focus', showBookDropdown);
    document.getElementById('bookInput').addEventListener('blur', function() {
        setTimeout(hideBookDropdown, 200);
    });

    document.getElementById('uploadTrigger').addEventListener('click', triggerImageUpload);
    document.getElementById('imageUpload').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file && file.type.match('image.*')) {
            processImage(file);
        } else {
            showImageError('Per favore seleziona un file immagine valido');
        }
    });

    const dropArea = document.getElementById('imageUploadContainer');
    dropArea.addEventListener('dragover', handleDragOver);
    dropArea.addEventListener('dragleave', handleDragLeave);
    dropArea.addEventListener('drop', handleDrop);

    document.getElementById('authorForm').addEventListener('submit', handleSubmit);
});

function populateBookDropdown() {
    const dropdown = document.getElementById('bookDropdown');
    dropdown.innerHTML = '';
    books.forEach(book => {
        const item = document.createElement('div');
        item.className = 'dropdown-item';
        item.textContent = book;
        item.addEventListener('mousedown', () => selectBook(book));
        dropdown.appendChild(item);
    });
}

function filterBooks() {
    const input = document.getElementById('bookInput').value.toLowerCase();
    document.querySelectorAll('.dropdown-item').forEach(item => {
        item.style.display = item.textContent.toLowerCase().includes(input) ? 'block' : 'none';
    });
    if (input.length > 0) showBookDropdown();
}

function showBookDropdown() {
    document.getElementById('bookDropdown').style.display = 'block';
}

function hideBookDropdown() {
    document.getElementById('bookDropdown').style.display = 'none';
}

function selectBook(bookTitle) {
    if (!selectedBooks.includes(bookTitle)) {
        selectedBooks.push(bookTitle);
        renderSelectedBooks();
    }
    document.getElementById('bookInput').value = '';
    hideBookDropdown();
}

function removeBook(bookTitle) {
    selectedBooks = selectedBooks.filter(b => b !== bookTitle);
    renderSelectedBooks();
}

function renderSelectedBooks() {
    const container = document.getElementById('selectedBooks');
    container.innerHTML = '';
    selectedBooks.forEach(book => {
        const item = document.createElement('div');
        item.className = 'selected-item';
        item.innerHTML = `${book} <button class="remove-item" onclick="removeBook('${book}')">&times;</button>`;
        container.appendChild(item);
    });
}

function triggerImageUpload() {
    document.getElementById('imageUpload').click();
}

function handleDragOver(e) {
    e.preventDefault();
    e.currentTarget.classList.add('hover');
}

function handleDragLeave(e) {
    e.preventDefault();
    e.currentTarget.classList.remove('hover');
}

function handleDrop(e) {
    e.preventDefault();
    e.stopPropagation();
    e.currentTarget.classList.remove('hover');
    const file = e.dataTransfer.files[0];
    if (file && file.type.match('image.*')) {
        processImage(file);
    } else {
        showImageError('Per favore seleziona un file immagine valido');
    }
}

function processImage(file) {
    if (selectedImage) {
        document.getElementById('imagePreview').innerHTML = '';
    }
    const reader = new FileReader();
    reader.onload = function(e) {
        const img = document.createElement('img');
        img.className = 'image-preview';
        img.src = e.target.result;
        const removeBtn = document.createElement('div');
        removeBtn.className = 'remove-image';
        removeBtn.innerHTML = '&times;';
        removeBtn.onclick = function() {
            document.getElementById('imagePreview').innerHTML = '';
            document.getElementById('imageUpload').value = '';
            selectedImage = null;// Lista "books" viene passata dal template Thymeleaf
let selectedBooks = [];
let selectedImage = null;

document.addEventListener('DOMContentLoaded', function() {
    populateBookDropdown();
    document.getElementById('bookInput').addEventListener('input', filterBooks);
    document.getElementById('bookInput').addEventListener('focus', showBookDropdown);
    document.getElementById('bookInput').addEventListener('blur', function() {
        setTimeout(hideBookDropdown, 200);
    });

    document.getElementById('uploadTrigger').addEventListener('click', triggerImageUpload);
    document.getElementById('imageUpload').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file && file.type.match('image.*')) {
            processImage(file);
        } else {
            showImageError('Per favore seleziona un file immagine valido');
        }
    });

    const dropArea = document.getElementById('imageUploadContainer');
    dropArea.addEventListener('dragover', handleDragOver);
    dropArea.addEventListener('dragleave', handleDragLeave);
    dropArea.addEventListener('drop', handleDrop);

    document.getElementById('authorForm').addEventListener('submit', handleSubmit);
});

function populateBookDropdown() {
    const dropdown = document.getElementById('bookDropdown');
    dropdown.innerHTML = '';
    books.forEach(book => {
        const item = document.createElement('div');
        item.className = 'dropdown-item';
        item.textContent = book;
        item.addEventListener('mousedown', () => selectBook(book));
        dropdown.appendChild(item);
    });
}

function filterBooks() {
    const input = document.getElementById('bookInput').value.toLowerCase();
    document.querySelectorAll('.dropdown-item').forEach(item => {
        item.style.display = item.textContent.toLowerCase().includes(input) ? 'block' : 'none';
    });
    if (input.length > 0) showBookDropdown();
}

function showBookDropdown() {
    document.getElementById('bookDropdown').style.display = 'block';
}

function hideBookDropdown() {
    document.getElementById('bookDropdown').style.display = 'none';
}

function selectBook(bookTitle) {
    if (!selectedBooks.includes(bookTitle)) {
        selectedBooks.push(bookTitle);
        renderSelectedBooks();
    }
    document.getElementById('bookInput').value = '';
    hideBookDropdown();
}

function removeBook(bookTitle) {
    selectedBooks = selectedBooks.filter(b => b !== bookTitle);
    renderSelectedBooks();
}

function renderSelectedBooks() {
    const container = document.getElementById('selectedBooks');
    container.innerHTML = '';
    selectedBooks.forEach(book => {
        const item = document.createElement('div');
        item.className = 'selected-item';
        item.innerHTML = `${book} <button class="remove-item" onclick="removeBook('${book}')">&times;</button>`;
        container.appendChild(item);
    });
}

function triggerImageUpload() {
    document.getElementById('imageUpload').click();
}

function handleDragOver(e) {
    e.preventDefault();
    e.currentTarget.classList.add('hover');
}

function handleDragLeave(e) {
    e.preventDefault();
    e.currentTarget.classList.remove('hover');
}

function handleDrop(e) {
    e.preventDefault();
    e.stopPropagation();
    e.currentTarget.classList.remove('hover');
    const file = e.dataTransfer.files[0];
    if (file && file.type.match('image.*')) {
        processImage(file);
    } else {
        showImageError('Per favore seleziona un file immagine valido');
    }
}

function processImage(file) {
    if (selectedImage) {
        document.getElementById('imagePreview').innerHTML = '';
    }
    const reader = new FileReader();
    reader.onload = function(e) {
        const img = document.createElement('img');
        img.className = 'image-preview';
        img.src = e.target.result;
        const removeBtn = document.createElement('div');
        removeBtn.className = 'remove-image';
        removeBtn.innerHTML = '&times;';
        removeBtn.onclick = function() {
            document.getElementById('imagePreview').innerHTML = '';
            document.getElementById('imageUpload').value = '';
            selectedImage = null;
        };
        const container = document.createElement('div');
        container.className = 'image-actions';
        container.appendChild(img);
        container.appendChild(removeBtn);
        document.getElementById('imagePreview').appendChild(container);
        selectedImage = file;
    };
    reader.readAsDataURL(file);
}

function showImageError(message) {
    const el = document.getElementById('image-error');
    el.textContent = message;
    setTimeout(() => { el.textContent = ''; }, 5000);
}

function handleSubmit(e) {
    e.preventDefault();
    document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
    document.querySelectorAll('.input').forEach(el => el.classList.remove('input-error'));
    let valid = true;
    ['nome','cognome','nazionalita','dataNascita'].forEach(id => {
        const el = document.getElementById(id);
        if (!el.value.trim()) {
            document.getElementById(id+'-error').textContent =
                id === 'dataNascita'
                  ? 'La data di nascita è obbligatoria'
                  : `${el.previousElementSibling.textContent} è obbligatorio`;
            el.classList.add('input-error');
            valid = false;
        }
    });
    if (!valid) return;
    setTimeout(() => {
        alert('Autore salvato con successo!');
        document.getElementById('authorForm').reset();
        document.getElementById('imagePreview').innerHTML = '';
        selectedBooks = [];
        renderSelectedBooks();
        selectedImage = null;
    }, 1000);
}

        };
        const container = document.createElement('div');
        container.className = 'image-actions';
        container.appendChild(img);
        container.appendChild(removeBtn);
        document.getElementById('imagePreview').appendChild(container);
        selectedImage = file;
    };
    reader.readAsDataURL(file);
}

function showImageError(message) {
    const el = document.getElementById('image-error');
    el.textContent = message;
    setTimeout(() => { el.textContent = ''; }, 5000);
}

function handleSubmit(e) {
    e.preventDefault();
    document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
    document.querySelectorAll('.input').forEach(el => el.classList.remove('input-error'));
    let valid = true;
    ['nome','cognome','nazionalita','dataNascita'].forEach(id => {
        const el = document.getElementById(id);
        if (!el.value.trim()) {
            document.getElementById(id+'-error').textContent =
                id === 'dataNascita'
                    ? 'La data di nascita è obbligatoria'
                    : `${el.previousElementSibling.textContent} è obbligatorio`;
            el.classList.add('input-error');
            valid = false;
        }
    });
    if (!valid) return;
    setTimeout(() => {
        alert('Autore salvato con successo!');
        document.getElementById('authorForm').reset();
        document.getElementById('imagePreview').innerHTML = '';
        selectedBooks = [];
        renderSelectedBooks();
        selectedImage = null;
    }, 1000);
}
