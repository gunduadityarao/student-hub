document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.getElementById("sidebar");
  const toggleBtn = document.getElementById("toggle-btn");
  const mainContent = document.getElementById("main-content");

  // Sidebar toggle functionality
  toggleBtn.addEventListener("click", () => {
    sidebar.classList.toggle("closed");  // Toggle 'closed' class to hide/show the sidebar
    mainContent.classList.toggle("expanded");  // Optionally toggle class for main content adjustment
  });
});
// Function to render cart items
function renderCartItems() {
    const cartItemsDiv = document.getElementById("cart-items");
    cartItemsDiv.innerHTML = ''; // Clear any previous content

    if (cart.length === 0) {
        cartItemsDiv.innerHTML = '<p>Your cart is empty!</p>';
        return;
    }

    cart.forEach((item, index) => {
        const cartItem = document.createElement('div');
        cartItem.classList.add('cart-item');
        cartItem.innerHTML = `
            <img src="https://via.placeholder.com/100" alt="${item.name}">
            <div class="cart-item-details">
                <h3>${item.name}</h3>
                <p>$${item.price}</p>
            </div>
            <div class="cart-controls">
                <span class="cart-quantity">${item.quantity}</span>
                <button class="decrease" data-index="${index}">-</button>
                <button class="increase" data-index="${index}">+</button>
            </div>
        `;
        cartItemsDiv.appendChild(cartItem);
    });
}

// Update cart event listeners for + and -
document.getElementById("cart-items").addEventListener("click", function(event) {
    const index = event.target.getAttribute("data-index");

    if (event.target.classList.contains("increase")) {
        cart[index].quantity++;
    } else if (event.target.classList.contains("decrease")) {
        if (cart[index].quantity > 1) {
            cart[index].quantity--;
        } else {
            cart.splice(index, 1);
        }
    }

    renderCartItems();
    localStorage.setItem('cart', JSON.stringify(cart));
});

// Initial render
renderCartItems();
