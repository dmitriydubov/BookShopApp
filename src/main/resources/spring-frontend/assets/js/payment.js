{
    const paymentForm = document.getElementById('payment-form');

    paymentForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const sum = document.getElementById('sum');
        $.post('/api/payment', {
            hash: e.srcElement.dataset.sendhash,
            sum: $(sum).val(),
            time: new Date().getTime()
        }, (payment) => {
            if (payment.status === 'pending') {
                const confirmationUrl = payment.confirmation.confirmation_url;
                window.location.href = confirmationUrl;
            } else {
                createResponseMessage('Error payment');
            }
        });
    });

    function createResponseMessage(message) {
        const topUpWrap = document.querySelector('.Topup-wrap');
        const formGroup = document.createElement('div');
        formGroup.classList.add('form-error');
        formGroup.append(message);
        topUpWrap.append(formGroup);
    }
}