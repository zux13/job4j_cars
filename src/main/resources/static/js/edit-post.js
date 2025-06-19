document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("toggleSoldBtn");
    const hidden = document.getElementById("soldHidden");

    button.addEventListener("click", () => {
        const isSold = hidden.value === "true";

        hidden.value = String(!isSold);
        button.textContent = isSold ? "Активно" : "Продано";
        button.classList.toggle("btn-outline-danger", !isSold);
        button.classList.toggle("btn-outline-success", isSold);
    });
});
