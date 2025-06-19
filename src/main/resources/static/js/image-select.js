document.addEventListener("DOMContentLoaded", () => {
  const photoInput = document.getElementById("photoInput");
  const previewContainer = document.getElementById("preview");
  const removedFileIdsInput = document.getElementById("removedFileIds");

  // Массив выбранных новых файлов
  let selectedFiles = [];

  // Массив id удалённых старых фото
  let removedFileIds = [];

  // Обработчик удаления (старые и новые фото)
  previewContainer.addEventListener("click", (e) => {
    const isRemoveOld = e.target.classList.contains("remove-existing-photo");
    const isRemoveNew = e.target.classList.contains("remove-new-photo");

    if (isRemoveOld || isRemoveNew) {
      const previewBlock = e.target.closest(".preview");

      if (!previewBlock) return;

      if (isRemoveOld) {
        const fileId = previewBlock.getAttribute("data-file-id");
        if (fileId && !removedFileIds.includes(fileId)) {
          removedFileIds.push(fileId);
          removedFileIdsInput.value = removedFileIds.join(",");
        }
      }

      if (isRemoveNew) {
        const index = parseInt(previewBlock.getAttribute("data-index"));
        if (!isNaN(index)) {
          selectedFiles.splice(index, 1);
          updateNewFilesPreview();
          updateInputFiles();
          return; // уже обновили DOM
        }
      }

      // Удаляем превью
      previewBlock.remove();

      if (window.initModal_editPhotoModal) {
        window.initModal_editPhotoModal();
      }

    }
  });

  // Обработчик изменения input file (новые файлы)
  photoInput.addEventListener("change", (e) => {
    const files = Array.from(e.target.files);

    files.forEach(file => {
      if (
        file.type.startsWith("image/") &&
        !selectedFiles.some(f => f.name === file.name && f.size === file.size)
      ) {
        selectedFiles.push(file);
      }
    });

    updateNewFilesPreview();
    updateInputFiles();
  });

  // Обновить превью новых файлов
  function updateNewFilesPreview() {
    // Удаляем старые превью новых фото
    previewContainer.querySelectorAll(".new-preview").forEach(el => el.remove());

    selectedFiles.forEach((file, index) => {
      const div = document.createElement("div");
      div.classList.add("preview", "new-preview");
      div.setAttribute("data-index", index);

      const img = document.createElement("img");
      img.src = URL.createObjectURL(file);
      img.style.width = "100%";
      img.style.height = "auto";
      img.onload = () => URL.revokeObjectURL(img.src);

      const removeButton = document.createElement("button");
      removeButton.className = "remove-btn remove-new-photo";
      removeButton.textContent = "×";

      div.appendChild(img);
      div.appendChild(removeButton);
      previewContainer.appendChild(div);
    });

      if (window.initModal_createPhotoModal) {
        window.initModal_createPhotoModal(); // обновляем модалку для создания
      }

      if (window.initModal_editPhotoModal) {
        window.initModal_editPhotoModal(); // обновляем модалку редактирования
      }

  }

  // Обновить files в input[type=file]
  function updateInputFiles() {
    const dataTransfer = new DataTransfer();
    selectedFiles.forEach(file => dataTransfer.items.add(file));
    photoInput.files = dataTransfer.files;
  }
});
