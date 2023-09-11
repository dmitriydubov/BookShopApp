{
    const profileForm = document.querySelector('.Profile-form');

    profileForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const profileFormAttrEndpoint = profileForm.getAttribute('action');

        const userNameFormElem = document.getElementById('name');
        const userEmailFormElem = document.getElementById('mail');
        const userPasswordFormElem = document.getElementById('password');
        const userPasswordReplyFormElem = document.getElementById('passwordReply');

        const formData = {
            name: $(userNameFormElem).val(),
            mail: $(userEmailFormElem).val(),
            password: $(userPasswordFormElem).val(),
            passwordReply: $(userPasswordReplyFormElem).val()
        };

        const responseMessage = document.getElementById('response-message');

        $.ajax({
            url: profileFormAttrEndpoint,
            method: 'POST',
            data: formData,
            success: (response) => {
                if (response.result === true) {
                    responseMessage.classList.remove('Profile-error');
                    responseMessage.classList.add('Profile-success');
                    responseMessage.textContent = response.message;
                } else {
                    responseMessage.classList.remove('Profile-success');
                    responseMessage.classList.add('Profile-error');
                    responseMessage.textContent = response.message;
                }
            }
        });
    });
}