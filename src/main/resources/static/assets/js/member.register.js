$(function(){
	/*-------
	*회원 가입
	*-------*/
	let checkId = 0; //아이디 중복 여부 저장 변수 (0은 중복체크 미실행, 또는 중복. 1은 미중복)
	let checkNick = 0; //닉넴 중복 여부 저장 변수 (^의 내용 그대로)
	
	//아이디 중복 체크
	$('#confirm_id').click(function(){
		if($('#id').val().trim() == '')
			{
				$('#message_id').css('color', 'red').text('아이디를 입력하세요.');
				$('#id').val('').focus();
				return;
			}
			
			$('#message_id').text(''); //메세지 초기화
			
			//서버와 비동기 통신
			$.ajax({
				url: 'confirmId/' + $('#id').val(),
				type: 'get',
				dataType: 'json',
				success: function(param)
				{
					if(param.result == 'idDuplicated')
						{
							checkId = 0;
							$('#message_id').css('color', 'red').text('중복된 아이디 입니다.');
							$('#id').val('').focus();
							
						}
					else if(param.result == 'notMatchPattern')
						{
							checkId = 0;
							$('#message_id').css('color', 'red').text('아이디가 주어진 패턴과 일치하지 않습니다.');
							$('#id').val('').focus();
						}
					else if(param.result == 'idnotFound')
						{
							checkId = 1;
							$('#message_id').css('color', 'green').text('등록 가능합니다.');
						}
					else
					{
						checkId = 0;
						alert('아이디 중복 체크 오류!');
					}
				},
				error:function(){
					checkId = 0;
					alert('네트워크 오류 발생!');
				}
			});
	});
	
	
	
	//닉네임 중복 체크
	$('#confirm_nick').click(function(){
			if($('#nick_name').val().trim() == '')
				{
					$('#message_nick').css('color', 'red').text('닉네임을 입력하세요.');
					$('#nick_name').val('').focus();
					return;
				}
				
				$('#message_nick').text(''); //메세지 초기화
				
				//서버와 비동기 통신
				$.ajax({
					url: 'confirmNickName/' + $('#nick_name').val(),
					type: 'get',
					dataType: 'json',
					success: function(param)
					{
						if(param.result == 'nickDuplicated')
							{
								checkNick = 0;
								$('#message_nick').css('color', 'red').text('중복된 닉네임 입니다.');
								$('#nick_name').val('').focus();
								
							}
						else if(param.result == 'notMatchPattern')
							{
									checkNick = 0;
								$('#message_nick').css('color', 'red').text('닉네임이 주어진 패턴과 일치하지 않습니다.');
								$('#nick_name').val('').focus();
							}
						else if(param.result == 'nickNotFound')
							{
									checkNick = 1;
								$('#message_nick').css('color', 'green').text('등록 가능합니다.');
							}
						else
						{
								checkNick = 0;
							alert('닉네임 중복 체크 오류!');
						}
					},
					error:function(){
							checkNick = 0;
						alert('닉네임 오류 발생!');
					}
				});
		});
	
	//별명 입력 텍스트에 따라서 입력창 크기 및 중복 체크 버튼 토글
	$('#nick_name').on('input',function(){
		if($('#nick_name').val().length > 0)
			{
				//별명을 입력한 경우 
				$('#confirm_nick').show();
				$('#nick_name').css('width', 255);
			}
			else{
				//별명을 입력하지 않은경우
				initNickName();
			}
	});
	
	
	
	function initNickName()
	{
		$('#confirm_nick').hide();
		$('#nick_name').css('width', 350);
		$('#message_nick').text('');
		
	}
	
	
	//입력시 초기화
	
	$('#id, #nick_name').keyup(function(){
		if($(this).attr('id') == 'id')
			{checkId = 0;
				$('#message_id').text('');
			}
			else
			{
				checkNick = 0;
				$('#message_nick').text('');
			}
	});
	//제출
	$('#member_register').submit(function(){
		//아이디 중복 체크 필수
		if(checkId == 0)
			{
				$('#message_id').css('color', 'red').text('아이디 중복 체크 필수!!');
				if($('#id').val().trim() == '')
								{
									$('#id').val('').focus();
									
								}
								return false;
			}
		if($('#nick_name').val() != '' && checkNick == 0)
			{
				$('#message_nick').css('color', 'red').html('<div class="form-notice">별명 중복 체크 필수, 별명을 사용하지 않을 경우, 별명을 지우고 전송하세요');
				return false;
			}
			
			
	});
});