{
    const buyBtn = document.getElementById('buy-btn');
    const buyBtnBooksIdsAttr = buyBtn.getAttribute('booksIds');
    const totalPrice = document.getElementById('Total-price');
    const popup = document.querySelector('.popup');
    const popupImageSucceed = document.querySelector('.popup_img-successful');
    const popupImageError = document.querySelector('.popup_img-error');
    const popupText = document.querySelector('.popup_text');

    //===========================================================================================================
    buyBtn.addEventListener('click', (e) => {
        e.preventDefault();

        $.post('/api/order', {
            sum: totalPrice.textContent,
            booksIds: buyBtnBooksIdsAttr
        }, (response) => {
            if (response.hasOwnProperty('redirectUrl')) {
                window.location = response.redirectUrl;
            }
            if (response.result === true) {
                showSucceedOrderMessage();
            } else {
                showErrorOrderMessage(response);
            }
        });
    })

    //===========================================================================================================
    function showSucceedOrderMessage() {
        popup.style.transform = 'translateX(0%)';
        popup.style.opacity = '100';
        popupImageSucceed.classList.remove('hide');
        popupText.style.opacity = '100';
        popupText.innerHTML = 'Successful!';
        popupText.style.color = '#4fff04';
        setTimeout(() => redirect('/my'), 3000);
    }

    function showErrorOrderMessage(response) {
       popup.style.transform = 'translateX(0%)';
       popup.style.opacity = '100';
       popupImageError.classList.remove('hide');
       popupText.style.opacity = '100';
       popupText.innerHTML = response.error;
       popupText.style.color = '#d51616';
       setTimeout(() => hidePopup(), 3000);
    }

    function redirect(url) {
        window.location = url;
    }

    function hidePopup() {
        popup.style.transform = 'translateX(-1000%)';
        popup.style.opacity = '0';
        popupText.style.opacity = '0';
        popupImageError.classList.add('hide');
    }
}