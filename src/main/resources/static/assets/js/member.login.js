$(function(){
	/*-----------------------
	* 회원 로그인
	*-----------------------*/
	$('#member_login').submit(function(){
		//메세지 초기화
		//기존 메세지가 있으면 0.5초 동안 움직임
		$('.error-invalid').slideUp(500);
		//지금 생성된 메세지는 빠르게 움직임
		$('#error_id').text('아이디를 입력하세요').slideUp(0);
		$('#error_passwd').text('비밀번호를 입력하세요').slideUp(0);
		
		if($('#id').val().trim() == '' && $('#passwd').val().trim() == '')
			{
				$('#error_id').slideDown(500);
				$('#error_passwd').slideDown(500);
				$('#id').focus();
				return false;
			}
		if($('#id').val().trim() == '' && $('#passwd').val().trim() != '')
			{
				$('#error_id').slideDown(500);
				$('#id').focus();
				return false;
			}
		if($('#id').val().trim() != '' && $('#passwd').val().trim() == '')
			{			
				$('#error_passwd').slideDown(500);
				$('#passwd').focus();
				return false;
			}
		});
		
		$('#id').on('input', function(){
			$('#error_id, .error-invalid').slideUp(1000);
		});
		
		$('#passwd').on('input', function(){
			$('#error_passwd, .error-invalid').slideUp(1000);
		});
	
});