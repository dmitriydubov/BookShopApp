{
    const reviewForm = document.getElementById('formReview');
    const textReview = document.getElementById('textReview');
    const submitBtn = document.getElementById('sendReview');
    const loginBtn = document.getElementById('toComeInMail');
    const bookId = reviewForm.dataset.bookid;
    let textReviewValue = textReview.value;

    //==================================================================================================================
    submitBtn.addEventListener('click', (e) => {
        e.preventDefault();
        refreshForm();

        const result = textReviewValue.length > 1 && textReviewValue !== null ? true : false;

        $.post('/api/bookReview', {
            bookId: bookId,
            text: textReviewValue
        }, (data) => {
            if (result) {
                createSuccessfulMessage();
                setTimeout(() => {
                    location.reload();
                }, 1000);
            } else {
                createErrorMessage(data.error);
            }
        });

        textReview.value = '';
        textReviewValue = '';
    });

    textReview.addEventListener('input', (e) => {
        textReviewValue = e.target.value;
    });

    //==================================================================================================================
    function createSuccessfulMessage() {
        const formGroup = document.createElement('div');
        const commentSuccessSent = document.createElement('div');
        formGroup.classList.add('form-group');
        commentSuccessSent.classList.add('Comments-successSent');
        commentSuccessSent.innerHTML += 'Review has been sent successfully';

        formGroup.append(commentSuccessSent);

        reviewForm.append(formGroup)
    }

    function createErrorMessage(errorMessage) {
        textReview.classList.add('form-textarea_error');
        const formError = document.createElement('div');
        formError.classList.add('form-error');
        formError.innerHTML = errorMessage;

        reviewForm.append(formError);
    }

    function refreshForm() {
        if (document.querySelector('.form-group')) {
            document.querySelector('.form-group').remove();
        } else if (document.querySelector('.form-error')) {
            document.querySelector('.form-error').remove();
            textReview.classList.remove('form-textarea_error');
        }
    }

}