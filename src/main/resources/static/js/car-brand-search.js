document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("brandSearchInput");
    const brandList = document.getElementById("brandList");

    input.addEventListener("input", function () {
        const filter = input.value.toLowerCase();
        const items = brandList.querySelectorAll(".form-check");

        items.forEach(item => {
            const label = item.querySelector("label");
            const text = label.textContent.toLowerCase();
            item.style.display = text.includes(filter) ? "" : "none";
        });
    });
});