document.addEventListener('DOMContentLoaded', () => {
  const modalConfigs = [
    {
      modalId: 'photoModal',
      imageId: 'modalImage',
      prevId: 'prevImage',
      nextId: 'nextImage',
      imageSelector: '.preview-image, .img-thumbnail, .img-fluid.rounded.mb-3'
    },
    {
      modalId: 'createPhotoModal',
      imageId: 'createModalImage',
      prevId: 'createPrevImage',
      nextId: 'createNextImage',
      imageSelector: '#preview img'
    },
    {
      modalId: 'editPhotoModal',
      imageId: 'editModalImage',
      prevId: 'editPrevImage',
      nextId: 'editNextImage',
      imageSelector: '#preview img, .img-thumbnail, .img-fluid.rounded.mb-3'
    }
  ];

  modalConfigs.forEach(setupModal);
});


function setupModal({ modalId, imageId, prevId, nextId, imageSelector }) {
  const modalEl = document.getElementById(modalId);
  if (!modalEl) return;

  const modalImg = document.getElementById(imageId);
  const prevBtn = document.getElementById(prevId);
  const nextBtn = document.getElementById(nextId);
  const modal = new bootstrap.Modal(modalEl);
  let currentIndex = -1;
  let images = [];

  function showImage(index) {
    if (images.length === 0) return;
    currentIndex = (index + images.length) % images.length;
    modalImg.onload = () => {
      modal.show();
      modalImg.onload = null;
    };
    modalImg.src = images[currentIndex].src;
  }

  function setupImageListeners() {
    images.forEach((img, index) => {
      img.style.cursor = 'pointer';
      img.removeEventListener('click', img._modalClickHandler);
      img._modalClickHandler = () => showImage(index);
      img.addEventListener('click', img._modalClickHandler);
    });
  }

  // Переинициализировать список изображений и слушателей
  function initModalPreviewImages() {
    images = Array.from(document.querySelectorAll(imageSelector)).filter(img => img.src);
    setupImageListeners();
  }

  // Стрелки
  prevBtn?.addEventListener('click', () => showImage(currentIndex - 1));
  nextBtn?.addEventListener('click', () => showImage(currentIndex + 1));

  // Клавиатура
  document.addEventListener('keydown', e => {
    if (!modalEl.classList.contains('show')) return;
    if (e.key === 'ArrowLeft') showImage(currentIndex - 1);
    else if (e.key === 'ArrowRight') showImage(currentIndex + 1);
    else if (e.key === 'Escape') modal.hide();
  });

  // Первичная инициализация
  initModalPreviewImages();

  // Делаем доступной извне
  window[`initModal_${modalId}`] = initModalPreviewImages;
}

