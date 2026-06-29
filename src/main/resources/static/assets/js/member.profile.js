$(function () {

    // ==========================
    // 프로필 사진 수정 버튼
    // ==========================
    $('#photo_btn').click(function () {

        // 파일 선택창 열기
        $('#upload').trigger('click');

        // 이미지 미리보기
        customViewImage(
            '#upload',
            '.my-photo',
            ['png', 'jpg', 'jpeg', 'gif'],
            1024 * 1024
        );

    });

    // ==========================
    // 프로필 사진 업로드
    // ==========================
    $('#photo_submit').click(function () {

        // 파일 선택 여부 확인
        if ($('#upload').val() == '') {
            alert('파일을 선택하세요.');
            return;
        }

        // FormData 생성
        const form_data = new FormData();

        // 선택한 파일 추가
        form_data.append('upload', $('#upload')[0].files[0]);

        $.ajax({
            url: '../member/updateMyPhoto',
            type: 'PUT',
            data: form_data,
            contentType: false,
            processData: false,

            // CSRF 토큰 추가
            beforeSend: function (xhr) {
                xhr.setRequestHeader(
                    $('meta[name="csrf-header"]').attr('content'),
                    $('meta[name="csrf-token"]').attr('content')
                );
            },

            // 업로드 성공
            success: function (param) {

                if (param.result == 'success') {

                    alert('프로필 사진이 변경되었습니다.');
					//파일 업로드 태그의 선택자, 업로드되는 이미지가 보여지는 태그 선택자
				customInitImage('#upload', '.my-photo');

                } else {
                    alert('파일 전송 오류');
                }

            },

            // 업로드 실패
            error: function () {
                alert('네트워크 오류 발생');
            }

        });

    });

    // ==========================
    // 취소 버튼
    // ==========================
    $('#photo_reset').click(function () {

        $('#upload').val('');

    });

});