
document.addEventListener('DOMContentLoaded', function () {
    const categorySelect = document.getElementById('category');
    const typeSelect = document.getElementById('type');

    const allTypes = window.typesJson || [];

    const typesByCategory = {};
    allTypes.forEach(type => {
        const catId = String(type.categoryId || '');
        if (!typesByCategory[catId]) typesByCategory[catId] = [];
        typesByCategory[catId].push(type);
    });

    function updateTypeOptions(categoryId, selectedTypeId = null) {
        typeSelect.innerHTML = '';
        typeSelect.disabled = true;

        if (!categoryId || !typesByCategory[categoryId]) {
            const option = new Option('Выберите категорию', '', true, true);
            option.disabled = true;
            typeSelect.appendChild(option);
            return;
        }

        const defaultOption = new Option('Не выбрано', '');
        typeSelect.appendChild(defaultOption);

        typesByCategory[categoryId].forEach(type => {
            const isSelected = selectedTypeId && String(type.id) === String(selectedTypeId);
            const option = new Option(type.name, type.id, isSelected, isSelected);
            typeSelect.appendChild(option);
        });

        typeSelect.disabled = false;
    }

    categorySelect.addEventListener('change', () => {
        updateTypeOptions(categorySelect.value);
    });

    // При загрузке
    const initialCategoryId = categorySelect.value;
    const initialTypeId = typeSelect.dataset.initialValue;

    if (initialCategoryId) {
        updateTypeOptions(initialCategoryId, initialTypeId);
    } else {
        // Либо просто оставить типы пустыми
        typeSelect.innerHTML = '<option value="" disabled selected>Выберите категорию</option>';
        typeSelect.disabled = true;
    }
});

