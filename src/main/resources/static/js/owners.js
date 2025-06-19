document.addEventListener("DOMContentLoaded", function () {
    const ownersList = document.getElementById("ownersList");
    const addOwnerBtn = document.getElementById("addOwnerBtn");
    const form = document.querySelector("form");
    const removedOwnerIdsContainer = document.getElementById("removedOwnersIds");

    // Добавление нового владельца
    addOwnerBtn.addEventListener("click", function () {
        const ownerTemplate = document.createElement("div");
        ownerTemplate.classList.add("owner-entry", "row", "g-2", "mb-3");

        ownerTemplate.innerHTML = `
            <input type="hidden" name="" value="">
            <input type="hidden" name="" value="">
            <input type="hidden" name="" value="">
            <div class="col-md-5">
                <input type="text" class="form-control" placeholder="ФИО владельца" required>
            </div>
            <div class="col-md-3">
                <input type="date" class="form-control date-start" required>
            </div>
            <div class="col-md-3">
                <input type="date" class="form-control date-end">
            </div>
            <div class="col-md-1 d-flex align-items-center">
                <button type="button" class="btn btn-danger btn-sm remove-owner">×</button>
            </div>
        `;

        ownersList.appendChild(ownerTemplate);
        reindexOwnerFields();
        attachValidation(ownerTemplate);
    });

    // Удаление владельца
    ownersList.addEventListener("click", function (e) {
        if (e.target.classList.contains("remove-owner")) {
            const entry = e.target.closest(".owner-entry");
            const entries = document.querySelectorAll(".owner-entry");
            if (entries.length <= 1) return;

            const hiddenInputs = entry.querySelectorAll("input[type='hidden']");
            if (hiddenInputs.length >= 3) {
                const ownerId = hiddenInputs[0].value;
                const carId = hiddenInputs[1].value;
                const historyId = hiddenInputs[2].value;

                if (ownerId && carId && historyId) {
                    addRemovedOwnerId(ownerId, carId, historyId);
                }
            }

            entry.remove();
            reindexOwnerFields();
            validateDates();
        }
    });

    // Добавление ID удалённого владельца
    function addRemovedOwnerId(ownerId, carId, historyId) {
        const index = removedOwnerIdsContainer.childElementCount;

        removedOwnerIdsContainer.insertAdjacentHTML("beforeend", `
            <input type="hidden" name="removedOwnersIds[${index}].ownerId" value="${ownerId}">
            <input type="hidden" name="removedOwnersIds[${index}].carId" value="${carId}">
            <input type="hidden" name="removedOwnersIds[${index}].historyId" value="${historyId}">
        `);
    }

    // Перенумерация имён полей
    function reindexOwnerFields() {
        const entries = document.querySelectorAll(".owner-entry");
        entries.forEach((entry, index) => {
            const hiddenInputs = entry.querySelectorAll("input[type='hidden']");
            if (hiddenInputs.length >= 3) {
                hiddenInputs[0].name = `ownershipHistories[${index}].id.ownerId`;
                hiddenInputs[1].name = `ownershipHistories[${index}].id.carId`;
                hiddenInputs[2].name = `ownershipHistories[${index}].id.historyId`;
            }

            const nameInput = entry.querySelector("input[type='text']");
            const startInput = entry.querySelector(".date-start");
            const endInput = entry.querySelector(".date-end");

            if (nameInput) nameInput.name = `ownershipHistories[${index}].owner.name`;
            if (startInput) startInput.name = `ownershipHistories[${index}].period.startAt`;
            if (endInput) endInput.name = `ownershipHistories[${index}].period.endAt`;
        });
    }

    // Валидация дат
    function validateDates() {
        let isValid = true;

        document.querySelectorAll(".invalid-feedback").forEach(el => el.remove());

        const entries = document.querySelectorAll(".owner-entry");
        const periods = [];
        let openPeriodCount = 0;

        entries.forEach((entry) => {
            const startInput = entry.querySelector(".date-start");
            const endInput = entry.querySelector(".date-end");

            if (!startInput) return;

            const start = startInput.value ? new Date(startInput.value) : null;
            const end = endInput?.value ? new Date(endInput.value) : null;

            startInput.classList.remove("is-invalid");
            endInput?.classList.remove("is-invalid");

            if (start && end && end < start) {
                markInvalid(startInput, "Дата начала позже даты окончания");
                markInvalid(endInput, "Дата окончания раньше даты начала");
                isValid = false;
            }

            if (start) {
                if (!end) openPeriodCount++;
                periods.push({ entry, startInput, endInput, start, end });
            }
        });

        // Проверка: только один открытый период
        if (openPeriodCount > 1) {
            periods.forEach(({ endInput }) => {
                if (!endInput?.value) {
                    markInvalid(endInput, "Может быть только один открытый период");
                }
            });
            isValid = false;
        }

        // Проверка на пересечения
        for (let i = 0; i < periods.length; i++) {
            for (let j = i + 1; j < periods.length; j++) {
                const a = periods[i];
                const b = periods[j];

                const aEnd = a.end || new Date(8640000000000000);
                const bEnd = b.end || new Date(8640000000000000);

                const overlap = a.start <= bEnd && b.start <= aEnd;

                if (overlap) {
                    // Помечаем только эти две записи
                    if (!a.entry.classList.contains("has-overlap")) {
                        markInvalid(a.startInput, "Период пересекается");
                        if (a.endInput) markInvalid(a.endInput, "Период пересекается");
                        a.entry.classList.add("has-overlap");
                    }

                    if (!b.entry.classList.contains("has-overlap")) {
                        markInvalid(b.startInput, "Период пересекается");
                        if (b.endInput) markInvalid(b.endInput, "Период пересекается");
                        b.entry.classList.add("has-overlap");
                    }

                    isValid = false;
                }
            }
        }

        return isValid;

        function markInvalid(input, message) {
            if (!input) return;
            input.classList.add("is-invalid");

            const feedback = document.createElement("div");
            feedback.className = "invalid-feedback";
            feedback.textContent = message;

            if (!input.nextElementSibling?.classList.contains("invalid-feedback")) {
                input.insertAdjacentElement("afterend", feedback);
            }
        }
    }

    // Вешаем обработчики на поля для валидации
    function attachValidation(entry) {
        const startInput = entry.querySelector(".date-start");
        const endInput = entry.querySelector(".date-end");

        if (startInput && endInput) {
            startInput.addEventListener("input", validateDates);
            endInput.addEventListener("input", validateDates);
        }
    }

    // Проверка при отправке формы
    form.addEventListener("submit", function (e) {
        if (!validateDates()) {
            e.preventDefault();
            alert("Дата окончания не может быть раньше даты начала у владельцев!");
        }
    });

    // Инициализация
    reindexOwnerFields();
    document.querySelectorAll(".owner-entry").forEach(attachValidation);
});

