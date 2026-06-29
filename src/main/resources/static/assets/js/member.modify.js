$(function(){
	//회원 정보 수정
	
	
	//별명 중복 여부 저장 변수
	let checkNick = 0;
	let nickName;
	
	//초기 별명 입력창 및 중복체크 버튼
	
	if($('#nick_name').val()!='')
		{
			nickName = $('#nick_name').val();
		}
		
		//별명 입력 텍스트에 따라서 입력창 크기 및 중복 체크 버튼 표시 여부 제어
		$('#nick_name').on('input', function(){
			if($('#nick_name').val() == nickName)
				{
					initNickName();
				}
			else if($('#nick_name').val().length > 0)
				{
					$('#confirm_nick').show();
					$('#nick_name').css('width', 255);
				}
				else {
					initNickName();
				}
		});
		
		
		function initNickName()
		{
			$('#confirm_nick').hide();
			$('#nick_name').css('width', 350);
			$('#message_nick').text('');
			
		}
		
		//별명 중복 체크
		$('#confirm_nick').click(function(){
			if($('#nick_name').val().trim() == '')
				{
					$('#message_nick').css('color', 'red').text('별명을 입력하세요.');
					$('#nick_name').val('').focus();
					return;
				}
			
				$('#message_nick').text(''); //메시지 초기화
				
				
				$.ajax({
					url: 'confirmNickName/' + $('#nick_name').val(),
					type:'get',
					dataType:'json',
					success:function(param){
						if(param.result == 'nickNotfound')
							{
								checkNick = 1;
								$('#message_nick').css('color', 'green').text('등록 가능 합니다.');
							}
							else if(param.result == 'nickNotDuplicated')
								{
									checkNick = 0;
									$('#message_nick').css('color', 'red').text('중복된 별명 입니다.');
									$('#nick_name').val('').focus();
								}
								else if(param.result == 'notMatchPattern')
									{
										checkNick = 0;
										$('#message_nick').css('color', 'red').text('패턴에 맞지 않습니다.');
										$('#nick_name').val('').focus();
									}
									else
									{
										checkNick = 0;
										alert('닉네임 변경 오류 발생!');
									}
					},
					error:function(){
						checkNick = 0;
						alert('네트워크 오류 발생!');
					}
					
				})
			
		});
		
		//별명중복 안내 메세지 초기화
		$('#nick_name').keyup(function(){
			checkNick = 0;
			$('#message_nick').text('');
			
		});
		
		//submit 이벤트 발생시 중복체크 여부 확인
		//attr('value')는 변경되기전의 데이터를 읽어옴
		$('#member_modify').submit(function(){
			if($('#nick_name').val() != '' && $('#nick_name').val()!=$('#nick_name').attr('value') && checkNick == 0)
				{
					$('#message_nick').css('color','red').html('<div class="form-notice">별명을 변경할경우 중복체크 필수!</div>');
					return false;
				}
			
		});
});